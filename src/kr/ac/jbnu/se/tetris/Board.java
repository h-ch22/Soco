package kr.ac.jbnu.se.tetris;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

public class Board extends JPanel implements ActionListener {
    int BoardWidth = 10;
    int BoardHeight = 22;

    Timer timer;
    Timer timer_easy;
    Timer timer_hard;
    boolean isFallingFinished = false;
    boolean isStarted = false;
    boolean isPaused = false;
    int numLinesRemoved = 0;
    int curX = 0;
    int curY = 0;
    JLabel statusbar;
    Shape curPiece;
    Tetrominoes[] board;
    Button reTry = new Button("Try Again");
    Tetris tetris;
    JRadioButton light = new JRadioButton("Light", true);
    JRadioButton dark = new JRadioButton("Dark", false);

    public Board(Tetris parent) {
        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(170, this);
        timer.start();

        statusbar = parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    tryMove(curPiece.rotateRight(), curX, curY);
                }

                else if(e.getButton() == MouseEvent.BUTTON3){
                    tryMove(curPiece.rotateLeft(), curX, curY);
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

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                dropDown();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int isMoveX = e.getX();

                if(isMoveX < 0){
                    tryMove(curPiece, curX - 1, curY);
                }

                if(isMoveX > 0){
                    tryMove(curPiece, curX + 1, curY);
                }
            }
        });

        clearBoard();
    }

    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    int squareWidth() {
        return (int) getSize().getWidth() / BoardWidth;
    }

    int squareHeight() {
        return (int) getSize().getHeight() / BoardHeight;
    }

    Tetrominoes shapeAt(int x, int y) {
        return board[(y * BoardWidth) + x];
    }

    long start;

    public void start() {
        reTry.setVisible(false);
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    private void pause() {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);

        if(!dark.isSelected()) {
            Color background_light = new Color(255,255,255);
            setBackground(background_light);

            Dimension size = getSize();
            int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

            for (int i = 0; i < BoardHeight; ++i) {
                for (int j = 0; j < BoardWidth; ++j) {
                    Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                    if (shape != Tetrominoes.NoShape)
                        drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }

            if (curPiece.getShape() != Tetrominoes.NoShape) {
                for (int i = 0; i < 4; ++i) {
                    int x = curX + curPiece.x(i);
                    int y = curY - curPiece.y(i);
                    drawSquare(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
                            curPiece.getShape());
                }
            }
        }

        if(dark.isSelected()){
            Color background_dark = new Color(68, 68 ,68);
            setBackground(background_dark);

            Dimension size = getSize();
            int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();

            for (int i = 0; i < BoardHeight; ++i) {
                for (int j = 0; j < BoardWidth; ++j) {
                    Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                    if (shape != Tetrominoes.NoShape)
                        drawSquare_dark(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
                }
            }

            if (curPiece.getShape() != Tetrominoes.NoShape) {
                for (int i = 0; i < 4; ++i) {
                    int x = curX + curPiece.x(i);
                    int y = curY - curPiece.y(i);
                    drawSquare_dark(g, 0 + x * squareWidth(), boardTop + (BoardHeight - y - 1) * squareHeight(),
                            curPiece.getShape());
                }
            }
        }
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }

    private void clearBoard() {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BoardWidth / 2 + 1;
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            long time = System.currentTimeMillis();
            timer.stop();
            isStarted = false;
            statusbar.setText("game over");
            reTry.setLocation(100,200);
            reTry.setSize(100,40);
            reTry.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    statusbar.setText("다시시도합니다");
                    start();
                }
            });
            add(reTry);
            reTry.setVisible(true);

            SimpleDateFormat DAY = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS");
            String date = DAY.format(new Date(time));

            curPiece.setShape(Tetrominoes.NoShape);
            String id = JOptionPane.showInputDialog(null, "이름을 입력하세요.", "사용자 등록", JOptionPane.OK_CANCEL_OPTION);

            try {
                FileWriter FW = new FileWriter("Statistics.txt", true);
                BufferedWriter BW = new BufferedWriter(FW);

                BW.write(id);
                BW.write(", " + date);
                BW.write(", " + numLinesRemoved);
                BW.newLine();
                BW.close();
            }

            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean tryMove(Shape newPiece, int newX, int newY) {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x < 0 || x >= BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;
        repaint();
        return true;
    }

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    public void drawSquare(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102), new Color(102, 204, 102),
                new Color(102, 102, 204), new Color(204, 204, 102), new Color(204, 102, 204), new Color(102, 204, 204),
                new Color(218, 170, 0) };

        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);

//        Color color2 = colors2[shape.ordinal()];


    }

    public void drawSquare_dark(Graphics g, int x, int y, Tetrominoes shape) {
        Color colors2[] = { new Color(255,173,197), new Color(184, 243, 184), new Color(255, 169, 176),
                new Color(204, 209, 255), new Color(255, 221, 166), new Color(252, 158, 189), new Color(198, 214, 247),
                new Color(171, 149, 212) };

        Color color = colors2[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(curPiece.rotateRight(), curX, curY);
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotateLeft(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case 'd':
                    oneLineDown();
                    break;
                case 'D':
                    oneLineDown();
                    break;
            }

        }
    }
}