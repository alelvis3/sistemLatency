package br.com.alelvis.sistemLatency.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultCaret;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import br.com.alelvis.sistemLatency.model.Host;

public class MonitorView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField txtNome;
	private JTextField txtEndereco;
	private JTable table;
	private DefaultCategoryDataset dataset;
	private JFreeChart graficoLatencia;
	private ChartPanel painelGrafico;
	private JTextArea textAreaAviso;
	private DefaultTableModel tableModel;
	private List<Host> hosts;
	private ScheduledExecutorService scheduler;
	private boolean isMonitoring = false;

	public MonitorView() {

		setTitle("Monitor Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panelGrafico = new JPanel();
		panelGrafico.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		panelGrafico.setBounds(10, 11, 764, 234);
		contentPane.add(panelGrafico);
		panelGrafico.setLayout(new BorderLayout(5, 5));

		JPanel panelHost = new JPanel();
		panelHost.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Host's",
				TitledBorder.CENTER, TitledBorder.TOP, null, Color.RED));
		panelHost.setBounds(10, 256, 338, 215);
		contentPane.add(panelHost);
		panelHost.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneHost = new JScrollPane();
		panelHost.add(scrollPaneHost);

		tableModel = new DefaultTableModel(new Object[] { "Nome", "Endereço", "Latência (ms)" }, 0);
		table = new JTable(tableModel);
		scrollPaneHost.setViewportView(table);

		JPanel panelAviso = new JPanel();
		panelAviso.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "Avisos",
				TitledBorder.CENTER, TitledBorder.TOP, null, new Color(255, 0, 0)));
		panelAviso.setBounds(358, 256, 416, 294);
		contentPane.add(panelAviso);
		panelAviso.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPaneAviso = new JScrollPane();
		scrollPaneAviso.setViewportBorder(new LineBorder(Color.RED));
		panelAviso.add(scrollPaneAviso);

		textAreaAviso = new JTextArea();
		scrollPaneAviso.setViewportView(textAreaAviso);
		DefaultCaret caret = (DefaultCaret) textAreaAviso.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JButton btnAdicionar = new JButton("Adicionar");
		btnAdicionar.setBounds(10, 527, 89, 23);
		contentPane.add(btnAdicionar);

		JButton btnMonitorar = new JButton("Monitorar");
		btnMonitorar.setBounds(208, 527, 140, 23);
		contentPane.add(btnMonitorar);

		JButton btnRemover = new JButton("Remover");
		btnRemover.setBounds(109, 527, 89, 23);
		contentPane.add(btnRemover);

		JLabel lblNome = new JLabel("Nome: ");
		lblNome.setBounds(10, 482, 59, 14);
		contentPane.add(lblNome);

		JLabel lblEndereco = new JLabel("Endereço:");
		lblEndereco.setBounds(10, 507, 59, 14);
		contentPane.add(lblEndereco);

		txtNome = new JTextField();
		txtNome.setBounds(79, 479, 269, 20);
		contentPane.add(txtNome);
		txtNome.setColumns(10);

		txtEndereco = new JTextField();
		txtEndereco.setColumns(10);
		txtEndereco.setBounds(79, 504, 269, 20);
		contentPane.add(txtEndereco);

		dataset = new DefaultCategoryDataset();
		graficoLatencia = ChartFactory.createLineChart("Gráfico de Latência", "Hora", "Latência (ms)", dataset,
				PlotOrientation.VERTICAL, true, true, false);
		painelGrafico = new ChartPanel(graficoLatencia);
		painelGrafico.setPreferredSize(new Dimension(400, 200));

		JPanel painelGraficoWrapper = new JPanel(new BorderLayout());
		painelGraficoWrapper.setBorder(
				new TitledBorder(null, "Gráfico de Latência", TitledBorder.LEADING, TitledBorder.TOP, null, Color.RED));
		painelGraficoWrapper.add(painelGrafico, BorderLayout.CENTER);
		panelGrafico.add(painelGraficoWrapper, BorderLayout.CENTER);

		hosts = new ArrayList<>();
		scheduler = Executors.newScheduledThreadPool(1);

		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nome = txtNome.getText();
				String endereco = txtEndereco.getText();
				if (!nome.isEmpty() && !endereco.isEmpty()) {
					tableModel.addRow(new Object[] { nome, endereco });
					hosts.add(new Host(nome, endereco));
					textAreaAviso.append("Host " + nome + " adicionado.\n");
				}
			}
		});

		btnRemover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0) {
					String nome = (String) tableModel.getValueAt(selectedRow, 0);
					tableModel.removeRow(selectedRow);
					hosts.removeIf(h -> h.getNome().equals(nome));
					textAreaAviso.append("Host " + nome + " removido.\n");
				}
			}
		});

		btnMonitorar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMonitoring) {
					scheduler.shutdownNow();
					textAreaAviso.append("Monitoramento parado.\n");
					btnMonitorar.setText("Monitorar");
					isMonitoring = false;
				} else {
					scheduler = Executors.newScheduledThreadPool(1);
					scheduler.scheduleAtFixedRate(new Runnable() {
						public void run() {
							monitorHosts();
						}
					}, 0, 5, TimeUnit.SECONDS);
					textAreaAviso.append("Monitoramento iniciado.\n");
					btnMonitorar.setText("Parar Monitoração");
					isMonitoring = true;
				}
			}
		});

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (!scheduler.isShutdown()) {
					scheduler.shutdownNow();
				}
			}
		}));
	}

	private void monitorHosts() {
		for (Host host : hosts) {
			try {
				InetAddress address = InetAddress.getByName(host.getEndereco());
				long start = System.currentTimeMillis();
				boolean reachable = address.isReachable(2000);
				long latency = System.currentTimeMillis() - start;

				if (reachable) {
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
					String currentTime = sdf.format(new Date());
					dataset.addValue(latency, host.getNome(), currentTime);
					textAreaAviso.append("Host " + host.getNome() + " alcançado em " + latency + "ms.\n");
					updateTableLatency(host.getNome(), latency);
				} else {
					textAreaAviso.append("Host " + host.getNome() + " não alcançável.\n");
					updateTableLatency(host.getNome(), -1);
				}
			} catch (IOException e) {
				textAreaAviso.append("Erro ao tentar alcançar o host " + host.getNome() + ": " + e.getMessage() + "\n");
				updateTableLatency(host.getNome(), -1);
			}
		}
	}

	private void updateTableLatency(String nome, long latency) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, 0).equals(nome)) {
				tableModel.setValueAt(latency >= 0 ? latency : "N/A", i, 2);
				break;
			}
		}
	}
}
