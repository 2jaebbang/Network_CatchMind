
// JavaObjClientView.java ObjecStram 기반 Client
//실질적인 채팅 창
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JToggleButton;
import javax.swing.JList;
import javax.swing.JOptionPane;

import java.awt.Canvas;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.Icon;
import javax.swing.JSlider;

public class JavaGameClientView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private static final int BUF_LEN = 128; // Windows 처럼 BUF_LEN 을 정의
	private Socket socket; // 연결소켓
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	private JLabel lblUserName;
	// private JTextArea textArea;
	private JTextPane textArea;

	private Frame frame;
	private FileDialog fd;
	private JButton imgBtn;

	// 문제 라벨
	JLabel answerLabel;

	// 내 점수
	JLabel score;
	
	// 라운드
	JLabel round;
	JLabel totalRound;
	
	JButton colorBtn;
	JButton RedBtn;
	JButton BlueBtn;
	JButton GreenBtn;
	JButton BlackBtn;
	JButton eraserBtn;
	JButton DotBtn;
	JButton LineBtn;
	JButton RectangleBtn;
	JButton RectangleBtn2;
	JButton CircleBtn;
	JButton CircleBtn2;
	JButton ResetBtn;
	JSlider slider;
	
	JLabel QuizImageBtn;
	JLabel QuizHintBtn;
	JLabel GameStartBtn;
	
	private JLabel lblNewLabel2;
	private JPanel panel_1;
	private JLabel timeLabel;
	
	//유저 리스트&점수 출력 라벨
	private JLabel[] UserLabel = new JLabel[3];
	private JLabel[] UserScoreLabel = new JLabel[3];
	
	JPanel panel;
	private Graphics gc;
	private int pen_size = 5; // minimum 2
	// 그려진 Image를 보관하는 용도, paint() 함수에서 이용한다.
	private Image panelImage = null;
	private Graphics gc2 = null;

	private Color selectedColor = new Color(0, 0, 255);
	private int shapeMode = 1; // dot:0, line:1, rectangle:2, circle:3

	// 마우스의 좌표
	private int old_x, old_y, new_x, new_y;
	
	//라운드
	private int roundCnt = 1;
	private int totalRoundCnt = 5;
	
	//퀴즈
	Quiz quiz;

	String currentQuiz;
	ImageIcon currentQuizImage;
	String currentQuizHint;
	
	//유저 리스트&점수리스트
	String[] userList;
	String[] userScoreList;
	boolean currentQuizImageToggle = false;
	
	Timer timer;

	/**
	 * Create the frame.
	 * 
	 * @throws BadLocationException
	 */
	public JavaGameClientView(String username, String ip_addr, String port_no) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1097, 734);
		contentPane = new JPanel() {
			Image backGround = new ImageIcon("src/img/mainBackground2.png").getImage();

			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(backGround, 0, 0, null);
				setOpaque(false);
				super.paintComponent(g);
			}
		};

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 108, 278, 471);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		scrollPane.setViewportView(textArea);
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));

		txtInput = new JTextField();
		txtInput.setBounds(74, 589, 209, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		btnSend.setBounds(295, 588, 69, 40);
		contentPane.add(btnSend);

		lblUserName = new JLabel("Name");
		lblUserName.setForeground(Color.WHITE);
		lblUserName.setBorder(new LineBorder(new Color(0, 0, 0)));
		lblUserName.setBackground(Color.WHITE);
		lblUserName.setFont(new Font("맑은 고딕", Font.BOLD, 35));
		lblUserName.setHorizontalAlignment(SwingConstants.CENTER);
		lblUserName.setBounds(12, 12, 124, 81);
		contentPane.add(lblUserName);
		setVisible(true);

		AppendText("User " + username + " connecting " + ip_addr + " " + port_no);
		UserName = username;
		lblUserName.setText(username);

		imgBtn = new JButton("+");
		imgBtn.setFont(new Font("굴림", Font.PLAIN, 16));
		imgBtn.setBounds(12, 587, 50, 40);
		contentPane.add(imgBtn);

		JLabel btnNewButton = new JLabel("", new ImageIcon("src/img/end.png"), JLabel.CENTER);
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnNewButton.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				SendObject(msg);
				System.exit(0);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		btnNewButton.setBounds(1002, 614, 69, 53);
		contentPane.add(btnNewButton);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBackground(Color.WHITE);
		panel.setBounds(302, 59, 615, 520);
		contentPane.add(panel);
		gc = panel.getGraphics();

		colorBtn = new JButton(new ImageIcon("src/img/rainbow.jpg"));
		colorBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		colorBtn.setBounds(376, 589, 75, 40);
		colorBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedColor = JColorChooser.showDialog(null, "Select a color", Color.BLACK);
			}
		});
		contentPane.add(colorBtn);

		RedBtn = new JButton("");
		RedBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		RedBtn.setBackground(Color.RED);
		RedBtn.setBounds(463, 589, 37, 40);
		RedBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedColor = Color.RED;
			}
		});
		contentPane.add(RedBtn);

		BlueBtn = new JButton("");
		BlueBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		BlueBtn.setBackground(Color.BLUE);
		BlueBtn.setBounds(512, 589, 37, 40);
		BlueBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedColor = Color.BLUE;
			}
		});
		contentPane.add(BlueBtn);

		GreenBtn = new JButton("");
		GreenBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		GreenBtn.setBackground(Color.GREEN);
		GreenBtn.setBounds(561, 589, 37, 40);
		GreenBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedColor = Color.GREEN;
			}
		});
		contentPane.add(GreenBtn);

		BlackBtn = new JButton("");
		BlackBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		BlackBtn.setBackground(Color.BLACK);
		BlackBtn.setBounds(610, 589, 37, 40);
		BlackBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedColor = Color.BLACK;
			}
		});
		contentPane.add(BlackBtn);

		eraserBtn = new JButton(new ImageIcon("src/img/eraser.png"));
		eraserBtn.setFont(new Font("굴림체", Font.PLAIN, 13));
		eraserBtn.setBounds(807, 589, 75, 40);
		eraserBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedColor = Color.WHITE;
			}
		});
		contentPane.add(eraserBtn);

		DotBtn = new JButton(new ImageIcon("src/img/dot.png"));
		DotBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeMode = 0;
			}
		});
		DotBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		DotBtn.setBounds(376, 647, 50, 40);
		contentPane.add(DotBtn);

		LineBtn = new JButton(new ImageIcon("src/img/line.png"));
		LineBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeMode = 1;
			}
		});
		LineBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		LineBtn.setBounds(438, 647, 50, 40);
		contentPane.add(LineBtn);

		RectangleBtn = new JButton(new ImageIcon("src/img/rect.png"));
		RectangleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeMode = 2;
			}
		});
		RectangleBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		RectangleBtn.setBounds(500, 647, 50, 40);
		contentPane.add(RectangleBtn);

		RectangleBtn2 = new JButton(new ImageIcon("src/img/rect2.png"));
		RectangleBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeMode = 3;
			}
		});
		RectangleBtn2.setFont(new Font("굴림체", Font.PLAIN, 14));
		RectangleBtn2.setBounds(561, 647, 50, 40);
		contentPane.add(RectangleBtn2);

		CircleBtn = new JButton(new ImageIcon("src/img/circle.png"));
		CircleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeMode = 4;
			}
		});
		CircleBtn.setFont(new Font("굴림체", Font.PLAIN, 14));
		CircleBtn.setBounds(620, 647, 50, 40);
		contentPane.add(CircleBtn);

		CircleBtn2 = new JButton(new ImageIcon("src/img/circle2.png"));
		CircleBtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shapeMode = 5;
			}
		});
		CircleBtn2.setFont(new Font("굴림체", Font.PLAIN, 14));
		CircleBtn2.setBounds(682, 647, 50, 40);
		contentPane.add(CircleBtn2);

		ResetBtn = new JButton(new ImageIcon("src/img/reset.png")); // 화면 초기화
		ResetBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reset();
				ChatMsg cm = new ChatMsg(UserName, "550", "Reset Canvas");
				SendObject(cm);
			}
		});
		ResetBtn.setBounds(744, 647, 43, 40);
		contentPane.add(ResetBtn);

		slider = new JSlider(2, 20, pen_size);
		slider.setMajorTickSpacing(5); // 큰 눈금 간격 5로 설정
		slider.setMinorTickSpacing(1); // 작은 눈금 간격 1로 설정
		slider.setPaintTicks(true); // 눈금을 표시한다.
		slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider js = (JSlider) e.getSource();
				pen_size = js.getValue();
			}
		});
		slider.setBounds(659, 589, 135, 32);
		contentPane.add(slider);

		GameStartBtn = new JLabel("", new ImageIcon("src/img/start.png"), JLabel.CENTER);

		GameStartBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				//quiz = new Quiz();
				ChatMsg obcm = new ChatMsg(UserName, "600", "게임시작");
			
				obcm.gameStart = true;
				SendObject(obcm);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});

		GameStartBtn.setFont(new Font("굴림", Font.PLAIN, 14));
		GameStartBtn.setBounds(39, 639, 105, 40);
		contentPane.add(GameStartBtn);

		QuizImageBtn = new JLabel("", new ImageIcon("src/img/quizImage.png"), JLabel.CENTER);

		QuizImageBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (currentQuizImageToggle == true) {
					currentQuizImageToggle = false;
					gc2.setColor(panel.getBackground());
					gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
					gc.drawImage(panelImage, 0, 0, panel);
				} else {
					currentQuizImageToggle = true;
					AppendImage(currentQuizImage);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		
		
		QuizHintBtn = new JLabel("", new ImageIcon("src/img/hint.png"), JLabel.CENTER);
		QuizHintBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				String tempScore = String.valueOf(Integer.parseInt(score.getText()) - 5);
				score.setText(tempScore);
				JOptionPane.showMessageDialog(panel, "힌트는 "+ currentQuizHint.toString()+"입니다.");
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
		QuizHintBtn.setFont(new Font("굴림", Font.PLAIN, 14));
		QuizHintBtn.setBounds(252, 639, 69, 40);
		contentPane.add(QuizHintBtn);
		

		QuizImageBtn.setFont(new Font("굴림", Font.PLAIN, 14));
		QuizImageBtn.setBounds(156, 639, 69, 40);
		contentPane.add(QuizImageBtn);

		JLabel quizLabel = new JLabel("<문제>");
		quizLabel.setForeground(Color.WHITE);
		quizLabel.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		quizLabel.setBounds(328, 10, 98, 39);
		contentPane.add(quizLabel);

		answerLabel = new JLabel("뭘까요");
		answerLabel.setForeground(Color.WHITE);
		answerLabel.setBackground(Color.LIGHT_GRAY);
		answerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		answerLabel.setBounds(452, 10, 254, 39);
		contentPane.add(answerLabel);

		UserLabel[0] = new JLabel("");
		UserLabel[0].setFont(new Font("맑은 고딕", Font.BOLD, 35));
		UserLabel[0].setForeground(Color.WHITE);
		UserLabel[0].setBorder(new LineBorder(new Color(0, 0, 0)));
		UserLabel[0].setBounds(929, 62, 142, 140);
		contentPane.add(UserLabel[0]);

		UserLabel[1] = new JLabel("");
		UserLabel[1].setForeground(Color.WHITE);
		UserLabel[1].setFont(new Font("맑은 고딕", Font.BOLD, 35));
		UserLabel[1].setBorder(new LineBorder(new Color(0, 0, 0)));
		UserLabel[1].setBounds(929, 250, 142, 140);
		contentPane.add(UserLabel[1]);

		UserLabel[2] = new JLabel("");
		UserLabel[2].setForeground(Color.WHITE);
		UserLabel[2].setFont(new Font("맑은 고딕", Font.BOLD, 35));
		UserLabel[2].setBorder(new LineBorder(new Color(0, 0, 0)));
		UserLabel[2].setBounds(929, 437, 142, 140);
		contentPane.add(UserLabel[2]);
		
//		UserScoreLabel[0] = new JLabel("test");
//		UserScoreLabel[0].setForeground(Color.WHITE);
//		UserScoreLabel[0].setFont(new Font("맑은 고딕", Font.BOLD, 35));
//		UserScoreLabel[0].setBorder(new LineBorder(new Color(0, 0, 0)));
//		UserScoreLabel[0].setBounds(929, 400, 60, 60);
//		contentPane.add(UserScoreLabel[0]);
//		
//		UserScoreLabel[1] = new JLabel("test");
//		UserScoreLabel[1].setForeground(Color.WHITE);
//		UserScoreLabel[1].setFont(new Font("맑은 고딕", Font.BOLD, 35));
//		UserScoreLabel[1].setBorder(new LineBorder(new Color(0, 0, 0)));
//		UserScoreLabel[1].setBounds(929, 500, 60, 60);
//		contentPane.add(UserScoreLabel[1]);
//		UserScoreLabel[2] = new JLabel("test");
//		UserScoreLabel[2].setForeground(Color.WHITE);
//		UserScoreLabel[2].setFont(new Font("맑은 고딕", Font.BOLD, 35));
//		UserScoreLabel[2].setBorder(new LineBorder(new Color(0, 0, 0)));
//		UserScoreLabel[2].setBounds(929, 600, 60, 60);
//		contentPane.add(UserScoreLabel[2]);
		

		JLabel lblNewLabel = new JLabel("내 점수");
		lblNewLabel.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBackground(Color.LIGHT_GRAY);
		lblNewLabel.setBounds(166, 12, 124, 37);
		contentPane.add(lblNewLabel);

		score = new JLabel("0");
		score.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		score.setForeground(Color.WHITE);
		score.setBackground(Color.LIGHT_GRAY);
		score.setBounds(198, 56, 69, 37);
		contentPane.add(score);
		
		round = new JLabel(Integer.toString(roundCnt));
		round.setForeground(Color.WHITE);
		round.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		round.setBackground(Color.LIGHT_GRAY);
		round.setBounds(946, 15, 37, 39);
		contentPane.add(round);
		
		lblNewLabel2 = new JLabel("/");
		lblNewLabel2.setForeground(Color.WHITE);
		lblNewLabel2.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		lblNewLabel2.setBackground(Color.LIGHT_GRAY);
		lblNewLabel2.setBounds(978, 15, 13, 39);
		contentPane.add(lblNewLabel2);
		
		totalRound = new JLabel(Integer.toString(totalRoundCnt));
		totalRound.setForeground(Color.WHITE);
		totalRound.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		totalRound.setBackground(Color.LIGHT_GRAY);
		totalRound.setBounds(1003, 15, 50, 39);
		contentPane.add(totalRound);
		
		timeLabel = new JLabel("60");
		timeLabel.setForeground(Color.WHITE);
		timeLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		timeLabel.setBounds(856, 15, 50, 32);
		contentPane.add(timeLabel);
		
		lblNewLabel_1 = new JLabel("제한시간");
		lblNewLabel_1.setFont(new Font("맑은 고딕", Font.BOLD, 25));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(732, 12, 112, 37);
		contentPane.add(lblNewLabel_1);
		
		
	
		

		// Image 영역 보관용. paint() 에서 이용한다.
		panelImage = createImage(panel.getWidth(), panel.getHeight());
		gc2 = panelImage.getGraphics();
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc2.setColor(Color.BLACK);
		gc2.drawRect(0, 0, panel.getWidth() - 1, panel.getHeight() - 1);

		try {
			socket = new Socket(ip_addr, Integer.parseInt(port_no));

			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.flush();
			ois = new ObjectInputStream(socket.getInputStream());

			// SendMessage("/login " + UserName);
			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
			SendObject(obcm);

			ListenNetwork net = new ListenNetwork();
			net.start();
			TextSendAction action = new TextSendAction();
			btnSend.addActionListener(action);
			txtInput.addActionListener(action);
			txtInput.requestFocus();
			ImageSendAction action2 = new ImageSendAction();
			imgBtn.addActionListener(action2);
			MyMouseEvent mouse = new MyMouseEvent();
			panel.addMouseMotionListener(mouse);
			panel.addMouseListener(mouse);
			MyMouseWheelEvent wheel = new MyMouseWheelEvent();
			panel.addMouseWheelListener(wheel);
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			AppendText("connect error");
		}
	}

	public void Reset() {
		gc2.setColor(panel.getBackground());
		gc2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		gc.drawImage(panelImage, 0, 0, panel);
	}

	public void paint(Graphics g) {
		super.paint(g);
		gc.drawImage(panelImage, 0, 0, this);
	}
	
	public void timeSet() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			int timeCnt = 60;
			public void run() {
				timeLabel.setText(Integer.toString(timeCnt));
				timeCnt--;
				if(timeCnt<0) {
					timer.cancel();
					SendObject(new ChatMsg(UserName, "650", "다음문제"));
				}
			}
		},0, 1000);
	}

	// Server Message를 수신해서 화면에 표시
	class ListenNetwork extends Thread {
		public void run() {
			while (true) {
				try {
					Object obcm = null;
					String msg = null;
					ChatMsg cm;
					try {
						obcm = ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
					if (obcm == null)
						break;
					if (obcm instanceof ChatMsg) {
						cm = (ChatMsg) obcm;
						msg = String.format("[%s]\n%s", cm.UserName, cm.data);
					} else
						continue;

					switch (cm.code) {
					case "200": // chat message
						if (cm.UserName.equals(UserName))
							AppendTextRight(msg); // 내 메세지는 우측에
						else
							AppendText(msg);
						break;
					case "300": // Image 첨부
						if (cm.UserName.equals(UserName))
							AppendTextRight("[" + cm.UserName + "]");
						else
							AppendText("[" + cm.UserName + "]");
						AppendImage(cm.img);
						break;
						
					case "350":
						userList = cm.data.split(" ");
						for(int i=0; i<UserLabel.length; i++) {
							UserLabel[i].setText("");
						}
						int j=0;
						for(int i=0; i<userList.length; i++) {
							//System.out.println("user is:"+userList[i]);
							if(userList[i].equals(UserName)) {
								continue;
							}
							UserLabel[j].setText("  "+userList[i]);
							j++;
						}
						break;
					case "500": // Mouse Event 수신
						DoMouseEvent(cm);
						break;

					case "550":
						Reset();
						break;

					case "650": // 게임시작
						timeSet();
						quiz = new Quiz();
						Reset();
						cm.gameStart = true;
						roundCnt++;
						GameStartBtn.setEnabled(false);
						//SendObject(new ChatMsg(UserName, "750", Integer.toString(cm.score)));
						for(int i=0; i<quiz.getQuiz().size(); i++) {
							if(cm.data.matches(quiz.getQuiz().get(i))) {
								System.out.println("cm.data: "+cm.data);
								currentQuizImage = quiz.getQuizImage().get(i);
								
								currentQuizHint = quiz.getQuizHint().get(i);
								System.out.println("quizHint: "+currentQuizHint);
								break;
							}
						}
						
						if (cm.UserName.equals(UserName)) {
							AppendTextRight("당신은 출제자입니다. 그림을 그리세요!");
							JOptionPane.showMessageDialog(panel, round.getText() + " / "+ totalRound.getText()+" 라운드를 시작합니다."+"\n"+"당신은 출제자입니다. 그림을 그리세요!");
							// 출제자는 채팅X
							txtInput.setEnabled(false);
							answerLabel.setText(cm.data);
							QuizImageBtn.setEnabled(true);
							QuizHintBtn.setEnabled(false);

						} else {
							AppendText("그림을 보고 문제를 맞추세요!");
							JOptionPane.showMessageDialog(panel, round.getText() + " / "+ totalRound.getText()+" 라운드를 시작합니다."+"\n"+"그림을 보고 문제를 맞추세요!");
							answerLabel.setText("뭘까요");

							txtInput.setEnabled(true);
							
							QuizImageBtn.setEnabled(false);
							QuizHintBtn.setEnabled(true);

							// 출제자가 아니므로 모든 버튼 false처리
							colorBtn.setEnabled(false);
							RedBtn.setEnabled(false);
							BlueBtn.setEnabled(false);
							GreenBtn.setEnabled(false);
							BlackBtn.setEnabled(false);
							eraserBtn.setEnabled(false);
							DotBtn.setEnabled(false);
							LineBtn.setEnabled(false);
							RectangleBtn.setEnabled(false);
							RectangleBtn2.setEnabled(false);
							CircleBtn.setEnabled(false);
							CircleBtn2.setEnabled(false);
							ResetBtn.setEnabled(false);
							slider.setEnabled(false);

							selectedColor = Color.white;
						}
						break;

					case "700": // 정답맞춤
						timer.cancel();
						AppendText("정답은 " + cm.data + "입니다!");
						AppendText(cm.UserName + "님이 문제를 맞췄습니다!");
						
						colorBtn.setEnabled(true);
						RedBtn.setEnabled(true);
						BlueBtn.setEnabled(true);
						GreenBtn.setEnabled(true);
						BlackBtn.setEnabled(true);
						eraserBtn.setEnabled(true);
						DotBtn.setEnabled(true);
						LineBtn.setEnabled(true);
						RectangleBtn.setEnabled(true);
						RectangleBtn2.setEnabled(true);
						CircleBtn.setEnabled(true);
						CircleBtn2.setEnabled(true);
						ResetBtn.setEnabled(true);
						slider.setEnabled(true);

						selectedColor = Color.blue;

						String tempRound = String.valueOf(Integer.parseInt(round.getText()) + 1);
						round.setText(tempRound);
						
						if (cm.UserName.equals(UserName)) {
							cm.score = Integer.parseInt(score.getText());
							cm.score+=10;
							String tempScore = String.valueOf(cm.score);
							score.setText(tempScore);

							if (Integer.parseInt(score.getText()) >= 20) {
								SendObject(new ChatMsg(UserName, "900", "20점"));
							} else if(roundCnt == 5){
								SendObject(new ChatMsg(UserName, "900", "라운드종료"));
							} else {
								SendObject(new ChatMsg(UserName, "650", "다음문제"));
							}	
						} 
						
						break;
					
					case "750":
//						userScoreList = cm.data.split(" ");
//						for(int i=0; i<UserScoreLabel.length; i++) {
//							UserScoreLabel[i].setText("");
//						}
//						int k=0;
//						for(int i=0; i<userList.length; i++) {
//							if(userList[i].equals(UserName)) {
//								continue;
//							}
//							UserScoreLabel[k].setText("  "+userScoreList[i]);
//							k++;
//						}
						break;
						
					case "800":
						if (cm.UserName.equals(UserName)) {
							
							JOptionPane.showMessageDialog(panel, "게임에서 승리했습니다!");
							GameStartBtn.setEnabled(true);
						}
						break;

					case "900":
						GameStartBtn.setEnabled(true);
						AppendText(cm.UserName + "님이 게임을 승리했습니다!");
						if (cm.UserName.equals(UserName)) {
							JOptionPane.showMessageDialog(panel, "게임에서 승리했습니다!");
						} else {
							JOptionPane.showMessageDialog(panel, score.getText() + "점입니다. 다음엔 더 잘할 거에요!");
						}
						score.setText("0");
						round.setText("1");
						roundCnt = 1;
						break;
					}
				} catch (IOException e) {
					AppendText("ois.readObject() error");
					try {
						ois.close();
						oos.close();
						socket.close();

						break;
					} catch (Exception ee) {
						break;
					} // catch문 끝
				} // 바깥 catch문끝

			}
		}
	}

	// Mouse Event 수신 처리
	public void DoMouseEvent(ChatMsg cm) {
		gc2.setColor(cm.color);
		// gc.setColor(cm.color);
		((Graphics2D) gc2).setStroke(new BasicStroke(cm.pen_size));
		if (cm.UserName.matches(UserName)) // 본인 것은 이미 Local 로 그렸다.
			return;

		if (cm.shapeMode == 0) { // dot
			gc2.fillOval(cm.mouse_e.getX() - pen_size / 2, cm.mouse_e.getY() - cm.pen_size / 2, cm.pen_size,
					cm.pen_size);
		} else if (cm.shapeMode == 1) { // line
			gc2.drawLine(cm.old_x, cm.old_y, cm.mouse_e.getX(), cm.mouse_e.getY());
		} else if (cm.shapeMode == 2) { // rec
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.drawRect(cm.old_x, cm.old_y, Math.abs(cm.old_x - cm.new_x), Math.abs(cm.old_y - cm.new_y));
			}
		} else if (cm.shapeMode == 3) { // fill rec
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.fillRect(cm.old_x, cm.old_y, Math.abs(cm.old_x - cm.new_x), Math.abs(cm.old_y - cm.new_y));
			}
		} else if (cm.shapeMode == 4) { // cir
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.drawOval(cm.old_x, cm.old_y, Math.abs(cm.old_x - cm.new_x), Math.abs(cm.old_y - cm.new_y));
			}
		} else if (cm.shapeMode == 5) { // fill cir
			if (cm.mouse_e.getID() == MouseEvent.MOUSE_RELEASED) {
				gc2.fillOval(cm.old_x, cm.old_y, Math.abs(cm.old_x - cm.new_x), Math.abs(cm.old_y - cm.new_y));
			}
		}
		gc.drawImage(panelImage, 0, 0, panel);
	}

	public void SendMouseEvent(MouseEvent e) {
		ChatMsg cm = new ChatMsg(UserName, "500", "MOUSE");
		cm.mouse_e = e;
		cm.pen_size = pen_size;
		cm.color = selectedColor;
		cm.shapeMode = shapeMode;
		cm.old_x = old_x;
		cm.old_y = old_y;
		cm.new_x = new_x;
		cm.new_y = new_y;
		SendObject(cm);
	}

	class MyMouseWheelEvent implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			if (e.getWheelRotation() < 0) { // 위로 올리는 경우 pen_size 증가
				if (pen_size < 20)
					pen_size++;
			} else {
				if (pen_size > 2)
					pen_size--;
			}
		}

	}

	// Mouse Event Handler
	class MyMouseEvent implements MouseListener, MouseMotionListener {
		@Override
		public void mouseDragged(MouseEvent e) {

			gc2.setColor(selectedColor);
			((Graphics2D) gc2).setStroke(new BasicStroke(pen_size));
			// gc.setColor(selectedColor);

			if (shapeMode == 0) {
				new_x = e.getX();
				new_y = e.getY();
				old_x = new_x;
				old_y = new_y;
				gc2.fillOval(e.getX() - pen_size / 2, e.getY() - pen_size / 2, pen_size, pen_size);
			} else if (shapeMode == 1) {
				new_x = e.getX();
				new_y = e.getY();
				old_x = new_x;
				old_y = new_y;
				gc2.drawLine(old_x, old_y, new_x, new_y);
			} else if (shapeMode == 2) {
			} else if (shapeMode == 3) {
			} else if (shapeMode == 4) {
			} else if (shapeMode == 5) {
			}
			// panelImnage는 paint()에서 이용한다.
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
//			gc.setColor(selectedColor);
//			gc.fillOval(e.getX()-pen_size/2, e.getY()-pen_size/2, pen_size, pen_size);
			old_x = e.getX();
			old_y = e.getY();
			SendMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// panel.setBackground(Color.YELLOW);

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// panel.setBackground(Color.CYAN);

		}

		@Override
		public void mousePressed(MouseEvent e) {

			old_x = e.getX();
			old_y = e.getY();

			SendMouseEvent(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			new_x = e.getX();
			new_y = e.getY();
			if (shapeMode == 2) {
				gc2.drawRect(old_x, old_y, Math.abs(old_x - new_x), Math.abs(old_y - new_y));
			} else if (shapeMode == 3) {
				gc2.fillRect(old_x, old_y, Math.abs(old_x - new_x), Math.abs(old_y - new_y));
			} else if (shapeMode == 4) {
				gc2.drawOval(old_x, old_y, Math.abs(old_x - new_x), Math.abs(old_y - new_y));
			} else if (shapeMode == 5) {
				gc2.fillOval(old_x, old_y, Math.abs(old_x - new_x), Math.abs(old_y - new_y));
			}
			gc.drawImage(panelImage, 0, 0, panel);
			SendMouseEvent(e);
		}
	}

	// keyboard enter key 치면 서버로 전송
	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}

	class ImageSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// 액션 이벤트가 sendBtn일때 또는 textField 에세 Enter key 치면
			if (e.getSource() == imgBtn) {
				frame = new Frame("이미지첨부");
				fd = new FileDialog(frame, "이미지 선택", FileDialog.LOAD);
				// frame.setVisible(true);
				// fd.setDirectory(".\\");
				fd.setVisible(true);
				// System.out.println(fd.getDirectory() + fd.getFile());
				if (fd.getDirectory().length() > 0 && fd.getFile().length() > 0) {
					ChatMsg obcm = new ChatMsg(UserName, "300", "IMG");
					ImageIcon img = new ImageIcon(fd.getDirectory() + fd.getFile());
					obcm.img = img;
					SendObject(obcm);
				}
			}
		}
	}

	ImageIcon icon1 = new ImageIcon("src/img/icon1.jpg");
	private JLabel lblNewLabel_1;
	private JLabel User3Label_1;



	public void AppendIcon(ImageIcon icon) {
		int len = textArea.getDocument().getLength();
		// 끝으로 이동
		textArea.setCaretPosition(len);
		textArea.insertIcon(icon);
	}

	// 화면에 출력
	public void AppendText(String msg) {
		// textArea.append(msg + "\n");
		// AppendIcon(icon1);
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		// textArea.setCaretPosition(len);
		// textArea.replaceSelection(msg + "\n");

		StyledDocument doc = textArea.getStyledDocument();
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setForeground(left, Color.BLACK);
		doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", left);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		// textArea.replaceSelection("\n");

	}

	// 화면 우측에 출력
	public void AppendTextRight(String msg) {
		msg = msg.trim(); //blank, \n을 제거.
		SimpleAttributeSet rightAlign = new SimpleAttributeSet();
		StyleConstants.setAlignment(rightAlign, StyleConstants.ALIGN_RIGHT);
		StyleConstants.setForeground(rightAlign, Color.RED);
		StyledDocument doc = textArea.getStyledDocument();
		doc.setParagraphAttributes(doc.getLength(), 1, rightAlign, false);
		try {
			doc.insertString(doc.getLength(), msg + "\n", rightAlign);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
	}

	public void AppendImage(ImageIcon ori_icon) {
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len); // place caret at the end (with no selection)
		Image ori_img = ori_icon.getImage();
		Image new_img;
		ImageIcon new_icon;
		int width, height;
		double ratio;
		width = ori_icon.getIconWidth();
		height = ori_icon.getIconHeight();
		// Image가 너무 크면 최대 가로 또는 세로 200 기준으로 축소시킨다.
		if (width > 200 || height > 200) {
			if (width > height) { // 가로 사진
				ratio = (double) height / width;
				width = 200;
				height = (int) (width * ratio);
			} else { // 세로 사진
				ratio = (double) width / height;
				height = 200;
				width = (int) (height * ratio);
			}
			new_img = ori_img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			new_icon = new ImageIcon(new_img);
			textArea.insertIcon(new_icon);
		} else {
			textArea.insertIcon(ori_icon);
			new_img = ori_img;
		}
		len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection("\n");
		// ImageViewAction viewaction = new ImageViewAction();
		// new_icon.addActionListener(viewaction); // 내부클래스로 액션 리스너를 상속받은 클래스로
		// panelImage = ori_img.getScaledInstance(panel.getWidth(), panel.getHeight(),
		// Image.SCALE_DEFAULT);
		gc2.drawImage(ori_img, 0, 0, panel.getWidth(), panel.getHeight(), panel);
		gc.drawImage(panelImage, 0, 0, panel.getWidth(), panel.getHeight(), panel);
	}

	// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
	public byte[] MakePacket(String msg) {
		byte[] packet = new byte[BUF_LEN];
		byte[] bb = null;
		int i;
		for (i = 0; i < BUF_LEN; i++)
			packet[i] = 0;
		try {
			bb = msg.getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		for (i = 0; i < bb.length; i++)
			packet[i] = bb[i];
		return packet;
	}

	// Server에게 network으로 전송
	public void SendMessage(String msg) {
		try {
			ChatMsg obcm = new ChatMsg(UserName, "200", msg);
			oos.writeObject(obcm);
		} catch (IOException e) {
			// AppendText("dos.write() error");
			AppendText("oos.writeObject() error");
			try {
//				dos.close();
//				dis.close();
				ois.close();
				oos.close();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(0);
			}
		}
	}

	public void SendObject(Object ob) { // 서버로 메세지를 보내는 메소드
		try {
			oos.writeObject(ob);
		} catch (IOException e) {
			// textArea.append("메세지 송신 에러!!\n");
			AppendText("SendObject Error");
		}
	}
}
