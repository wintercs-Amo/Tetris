package newcode;

import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

//小方块类型
class Cell{
	//坐标位置 是在wall[ROWS][COLS] 背景里的行 和 列
	public int row;
	public int col;
	BufferedImage image;
	public Cell(int row, int col, BufferedImage image) {
		super();
		this.row = row;
		this.col = col;
		this.image = image;
	}
}

//图形类的基类
class Tertrimino{
	public Cell[] cells;
	public Tertrimino(){
		cells = new Cell[4];//new数组对象
	}
	public void down(){
		for(int i=0;i<cells.length;i++){
			cells[i].row++;
		}
	}
	public void left(){
		for(int i=0;i<cells.length;i++){
			cells[i].col--;
		}
	}
	public void right(){
		for(int i=0;i<cells.length;i++){
			cells[i].col++;
		}
	}
	
	int getRow(int i){
		int row = cells[i].row;
		int col = cells[i].col;
		int cr = cells[0].row;
		int cc = cells[0].col;
		//(row,col)    中心点  (cr,cc)   求进行旋转之后 得到row
		if(row == cr){//同一水平线上
			return cr+col-cc;
		}
		if(col == cc){//同一垂直线上
			return cr;
		}
		if((col > cc && row > cr) || (col < cc && row < cr)){
			return row;
		}
		
		return cr + cr - row;
	}
	int getCol(int i){
		int row = cells[i].row;
		int col = cells[i].col;
		int cr = cells[0].row;
		int cc = cells[0].col;
		//(row,col)    中心点  (cr,cc)   求进行旋转之后 得到col
		if(row == cr){
			return cc;
		}
		if(col == cc){
			return cc - (row - cr);
		}
		if((col > cc && row > cr) || (col < cc && row < cr)){
			return cc - (col-cc);
		}
		return col;
	}
	//计算旋转之后小方块的位置
	public Cell[] getPosByRotation(){
		Cell[] afcells = new Cell[4];
		afcells[0] = cells[0];
		//(cells[0].row,cells[0].col)
		for(int i=1;i<afcells.length;i++){
			afcells[i] = new Cell(getRow(i),getCol(i),cells[i].image);
		}
		return afcells;
	}
	
	public void rotation(){
		for(int i=1;i<cells.length;i++){
			cells[i] = new Cell(getRow(i),getCol(i),cells[i].image);
		}
	}

}

class I extends Tertrimino{
	public I(){
		cells[0] = new Cell(0,4,Tetris.I);
		cells[1] = new Cell(0,3,Tetris.I);
		cells[2] = new Cell(0,5,Tetris.I);
		cells[3] = new Cell(0,6,Tetris.I);
	}
//	public void rotation(){
//		
//	}
}

class O extends Tertrimino{
	public O(){
		cells[0] = new Cell(0,4,Tetris.O);
		cells[1] = new Cell(0,5,Tetris.O);
		cells[2] = new Cell(1,4,Tetris.O);
		cells[3] = new Cell(1,5,Tetris.O);
	}
	public void rotation(){
		
	}
}

class S extends Tertrimino{
	public S(){
		cells[0] = new Cell(0,4,Tetris.S);
		cells[1] = new Cell(0,5,Tetris.S);
		cells[2] = new Cell(1,4,Tetris.S);
		cells[3] = new Cell(1,3,Tetris.S);
	}
}
class T extends Tertrimino{
	public T(){
		cells[0] = new Cell(0,4,Tetris.T);
		cells[1] = new Cell(0,3,Tetris.T);
		cells[2] = new Cell(0,5,Tetris.T);
		cells[3] = new Cell(1,4,Tetris.T);
	}
}
class Z extends Tertrimino{
	public Z(){
		cells[0] = new Cell(0,4,Tetris.Z);
		cells[1] = new Cell(0,3,Tetris.Z);
		cells[2] = new Cell(1,4,Tetris.Z);
		cells[3] = new Cell(1,5,Tetris.Z);
	}
}
class J extends Tertrimino{
	public J(){
		cells[0] = new Cell(0,5,Tetris.J);
		cells[1] = new Cell(0,4,Tetris.J);
		cells[2] = new Cell(0,3,Tetris.J);
		cells[3] = new Cell(1,5,Tetris.J);
	}
}

class L extends Tertrimino{
	public L(){
		cells[0] = new Cell(0,4,Tetris.L);
		cells[1] = new Cell(0,5,Tetris.L);
		cells[2] = new Cell(0,6,Tetris.L);
		cells[3] = new Cell(1,4,Tetris.L);
	}
}

public class Tetris extends JPanel{
	static BufferedImage background;
	static BufferedImage I,O,S,T,Z,J,L;
	final static int SIZE=26;
	final static int ROWS=20;
	final static int COLS=10;
	static{
		try {
			background = ImageIO.read(Tetris.class.getResource("pic/tetris.png"));
			I = ImageIO.read(Tetris.class.getResource("pic/I.png"));
			O = ImageIO.read(Tetris.class.getResource("pic/O.png"));
			S = ImageIO.read(Tetris.class.getResource("pic/S.png"));
			T = ImageIO.read(Tetris.class.getResource("pic/T.png"));
			Z = ImageIO.read(Tetris.class.getResource("pic/Z.png"));
			J = ImageIO.read(Tetris.class.getResource("pic/J.png"));
			L = ImageIO.read(Tetris.class.getResource("pic/L.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Tertrimino currtrimino;
	Tertrimino nexttrimino;
	Timer timer;
	Cell[][] wall;//背景墙 
	
	public Tetris(){
		maketrimino();
		wall = new Cell[ROWS][COLS];//null
	}
	
	public boolean canDown(){
		Cell[] cells = currtrimino.cells;
		for(int i=0;i<cells.length;i++){
			//已经到达最下端了不能下落
			if(cells[i].row >= ROWS-1){
				return false;
			}
			//小方块下面已经有方块了不能下落
			if(wall[cells[i].row+1][cells[i].col]!=null){
				return false;
			}
		}
		return true;
	}
	public void landToWall(){
		Cell[] cells = currtrimino.cells;
		for(int i=0;i<cells.length;i++){
			wall[cells[i].row][cells[i].col] = cells[i];
		}
	}
	public boolean isFull(int row){
		for(int col=0;col<COLS;col++){
			if(wall[row][col]==null){
				return false;
			}
		}
		return true;
	}
	public void moveToNextRow(int row){
		for(int i=row;i>=0;i--){
			wall[i+1] = wall[i];
		}
		for(int j=0;j<COLS;j++){
			wall[0] = new Cell[COLS];
		}
	}
	//消除
	public void destroy(){
		for(int i=0;i<ROWS;i++){//遍历所有行
			if(isFull(i)){//第i行已经满了 第i行可以消除
				moveToNextRow(i-1);
			}
		}
	}
	class Task extends TimerTask{
		public void run(){
			if(canDown()){
				currtrimino.down();
			}else{
				//1.把下落的图形镶嵌到wall里面
				landToWall();
				//2.判断是否可以消除
				destroy();
				//3.生成新图形
				maketrimino();
			}
			repaint();
		}
	}
	public boolean canLeft(){
		Cell[] cells = currtrimino.cells;
		for(int i=0;i<cells.length;i++){
			if(cells[i].col <= 0){
				return false;
			}
			if(wall[cells[i].row][cells[i].col-1]!=null){
				return false;
			}
		}
		return true;
	}
	public boolean canRight(){
		Cell[] cells = currtrimino.cells;
		for(int i=0;i<cells.length;i++){
			if(cells[i].col >= COLS-1){
				return false;
			}
			if(wall[cells[i].row][cells[i].col+1]!=null){
				return false;
			}
		}
		return true;
	}
	public void left(){
		if(canLeft()){
			currtrimino.left();
		}
	}
	public void down(){
		if(canDown()){
			currtrimino.down();
		}
	}
	public void right(){
		if(canRight()){
			currtrimino.right();
		}
	}
	public void straigtDown(){
		while(canDown()){
			currtrimino.down();
		}
	}
	public boolean canRotation(Cell[] cells){
		for(int i=0;i<cells.length;i++){
			System.out.println(cells[i].row+","+cells[i].col);
			if(cells[i].row >= ROWS){
				return false;
			}
			if(cells[i].col < 0 || cells[i].col >= COLS){
				return false;
			}
			if(wall[cells[i].row][cells[i].col] != null){
				return false;
			}
		}
		return true;
	}
	public void rotation(){
		Cell[] afcells = currtrimino.getPosByRotation();
		if(canRotation(afcells)){
			currtrimino.rotation();
		}
	}
//	class MyListener implements KeyListener{
//
//		@Override
//		public void keyPressed(KeyEvent e) {
//			switch(e.getKeyCode()){
//			case KeyEvent.VK_LEFT:
//				left();break;
//			case KeyEvent.VK_RIGHT:
//				right();break;
//			case KeyEvent.VK_DOWN:
//				down();break;
//			case KeyEvent.VK_SPACE:
//				straigtDown();break;
//			case KeyEvent.VK_UP:
//				rotation();break;
//			}
//			
//		}
//
//		@Override
//		public void keyReleased(KeyEvent e) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void keyTyped(KeyEvent e) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	}
	//class KeyAdapter implements KeyListener  
	class MyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_LEFT:
				left();break;
			case KeyEvent.VK_RIGHT:
				right();break;
			case KeyEvent.VK_DOWN:
				down();break;
			case KeyEvent.VK_SPACE:
				straigtDown();break;
			case KeyEvent.VK_UP:
				rotation();break;
			case KeyEvent.VK_S:
				timer.cancel();
			}		
		}		
	}
	public void action(){
		timer = new Timer();
		timer.schedule(new Task(), 200, 200);
		this.addKeyListener(new MyListener());
		this.requestFocus();
	}
	public void makenexttrimino(){
		Random rand = new Random();
		switch(rand.nextInt(7)){
		case 0:
			nexttrimino = new O();break;
		case 1:
			nexttrimino = new I();break;
		case 2:
			nexttrimino = new S();break;
		case 3:
			nexttrimino = new T();break;
		case 4:
			nexttrimino = new Z();break;
		case 5:
			nexttrimino = new J();break;
		case 6:
			nexttrimino = new L();break;
		}
	}
	public void maketrimino(){
		if(nexttrimino == null){
			makenexttrimino();
		}
		currtrimino = nexttrimino;
		makenexttrimino();
	}
	
	public void paint(Graphics g){
		g.drawImage(background,0,0,null);
		
		g.translate(15, 15);
		Cell[] cells = currtrimino.cells;
		for(int i=0;i<cells.length;i++){
			g.drawImage(cells[i].image, cells[i].col*SIZE, cells[i].row*SIZE, null);
		}
		cells = nexttrimino.cells;
		for(int i=0;i<cells.length;i++){
			g.drawImage(cells[i].image, (cells[i].col+10)*SIZE, (cells[i].row+1)*SIZE, null);
		}
		//绘制背景wall
		for(int i=0;i<ROWS;i++){
			for(int j=0;j<COLS;j++){
				if(wall[i][j] != null){
					g.drawImage(wall[i][j].image, j*SIZE, i*SIZE, null);
				}
			}
		}
	}
	
	public static void main(String[] args) {
		JFrame window = new JFrame();
		window.setSize(background.getWidth(),background.getHeight());
		window.setLocationRelativeTo(null);
		window.setUndecorated(true);
		
		Tetris panel = new Tetris();
		window.add(panel);
		
		window.setVisible(true);
		panel.action();
	}
}
