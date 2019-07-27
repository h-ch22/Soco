package kr.ac.jbnu.se.tetris;

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.*;

public class Tetris extends JFrame{
    JLabel statusbar;
    JMenuBar Menu = new JMenuBar();
    JMenuItem newGame, Statistics, Option, EXIT;
    String str;
    char cur;

    public Tetris() throws UnsupportedAudioFileException, LineUnavailableException, IOException{
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        Board board = new Board(this);
        add(board);
        board.start();
        setSize(200, 400);

        JMenu game = new JMenu("게임");
        JMenuItem newGame = new JMenuItem("새 게임");
        JMenuItem Statistics = new JMenuItem("통계");
        JMenuItem Option = new JMenuItem("옵션");
        JMenuItem EXIT = new JMenuItem("종료");

        Menu.add(game);
        game.add(newGame);
        game.add(Statistics);
        game.addSeparator();
        game.add(Option);
        game.add(EXIT);

        File file = new File("src\\Tetris.wav");
        AudioInputStream AIS=AudioSystem.getAudioInputStream(file);
        Clip clip=AudioSystem.getClip();

        clip.stop();
        clip.open(AIS);
        clip.start();
        clip.loop(-1);

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = e.getActionCommand();
                if(str == "새 게임") {
                    int result = JOptionPane.showConfirmDialog(null, "새 게임을 시작하시겠습니까? 통계에는 저장되지 않습니다.", "새 게임", JOptionPane.YES_NO_OPTION);

                    if (result == 0) {
                        board.start();
                    }
                }
            }
        });

        Statistics.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = e.getActionCommand();
                if(str == "통계"){
                    try{
                        File Statistics = new File("Statistics.txt");
                        FileReader FR = new FileReader(Statistics);
                        int current = 0;
                        while((current = FR.read()) != -1){
                            System.out.print((char)current);
                        }


                        FR.close();
                    }

                    catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        Option.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = e.getActionCommand();
                if (str == "옵션") {
                    Frame op = new Frame("옵션");

                    op.setSize(300, 500);
                    op.setVisible(true);
                    op.setLocation(200, 200);
                    op.setLayout(new FlowLayout());
                    Color bg = new Color(238,238,238);
                    op.setBackground(bg);
                    JLabel difficult = new JLabel("난이도");
                    JRadioButton low = new JRadioButton("하", false);
                    JRadioButton medium = new JRadioButton("중", true);
                    JRadioButton high = new JRadioButton("상", false);
                    JRadioButton custom = new JRadioButton("사용자 지정", false);
                    setDefaultCloseOperation(EXIT_ON_CLOSE);

                    JLabel Theme = new JLabel("테마 설정");
//                    JRadioButton light = new JRadioButton("Light", true);
//                    JRadioButton dark = new JRadioButton("Dark", false);
                    ButtonGroup theme = new ButtonGroup();
                    theme.add(board.light);
                    theme.add(board.dark);

                    ButtonGroup difficulty = new ButtonGroup();
                    difficulty.add(low);
                    difficulty.add(medium);
                    difficulty.add(high);
                    difficulty.add(custom);

//                    dark.addItemListener(new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent e) {
//                            Color background_dark = new Color(68, 68 ,68);
//                            board.setBackground(background_dark);
//                        }
//                    });
//
//                    light.addItemListener(new ItemListener() {
//                        @Override
//                        public void itemStateChanged(ItemEvent e) {
//                            Color background_light = new Color(255,255,255);
//                            board.setBackground(background_light);
//                        }
//                    });

                    low.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if(low.isSelected()){
                                board.timer_easy = new Timer(240, board);
                                board.start();
                                board.timer.stop();
                                board.timer_easy.start();
                            }
                        }
                    });

                    medium.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if(medium.isSelected()){
                                board.timer_easy.stop();
                                board.timer_hard.stop();
                                board.start();
                            }
                        }
                    });

                    high.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if(high.isSelected()){
//                                setSize(400,800);
//                                board.BoardHeight = 44;
//                                board.BoardWidth = 20;
//                                board.start();
                                board.timer_hard = new Timer(100, board);
                                board.timer.stop();
                                board.timer_hard.start();
                            }
                        }
                    });

                    JCheckBox Enable_Sound = new JCheckBox("사운드 출력", true);

                    Enable_Sound.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if(Enable_Sound.isSelected()){
                                clip.start();
                            }

                            else{
                                clip.stop();
                            }
                        }
                    });

                    JCheckBox Mouse = new JCheckBox("마우스 사용", true);

                    JButton OK = new JButton("확인");
                    OK.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            op.dispose();
                        }
                    });

                    JLabel size = new JLabel("보드 크기 설정 (x, y)");
                    JTextField boardx = new JTextField(10);
                    JTextField boardy = new JTextField(10);

                    op.setLayout(null);

                    custom.setBounds(10,110,90,20);
                    difficult.setBounds(10, 30, 110, 20);
                    low.setBounds(10,50,50,20);
                    medium.setBounds(10,70,50,20);
                    high.setBounds(10,90,50,20);
                    Enable_Sound.setBounds(10,220,110,20);
                    OK.setBounds(90, 430, 100, 50);
                    size.setBounds(10,130,110,20);
                    boardx.setBounds(10,150,50,20);
                    boardy.setBounds(10,170,50,20);
                    Mouse.setBounds(10, 250,110, 20);
                    Theme.setBounds(10, 270,110,20);
                    board.light.setBounds(10,290,110,20);
                    board.dark.setBounds(10,310,110,20);

                    op.add(Theme);
                    op.add(board.light);
                    op.add(board.dark);
                    op.add(Mouse);
                    op.add(boardx);
                    op.add(boardy);
                    op.add(size);
                    op.add(OK);
                    op.add(Enable_Sound);
                    op.add(difficult);
                    op.add(low);
                    op.add(medium);
                    op.add(high);
                    op.add(custom);
                }
            }
        });

        EXIT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                str = e.getActionCommand();
                if (str == "종료") {
                    int result = JOptionPane.showConfirmDialog(null, "종료하시겠습니까? 통계에는 저장되지 않습니다.", "종료", JOptionPane.YES_NO_OPTION);

                    if (result == 0) {
                        System.exit(0);
                    }
                }
            }
        });

        setJMenuBar(Menu);
        setVisible(true);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public JLabel getStatusBar() {
        return statusbar;
    }


    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Tetris game = new Tetris();
        game.setLocationRelativeTo(null);
        game.setVisible(true);
    }
}