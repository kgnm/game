import java.applet.*;
import java.util.*;
import java.awt.*;
import java.lang.*;
import java.awt.event.*;
public class puyo extends Applet implements Runnable{
    Random rnd = new Random();
    int MX = 6, MY =12,p1_x,p1_y,p2_x,p2_y,random1=rnd.nextInt(4),random2=rnd.nextInt(4),count=0,flag[][],nc1,nc2,paint=0,rensa,hyouji;
    char map[][];
    Thread th=null;
    Boolean chigiri=false,vani=false;
    Image puyoImg[]= new Image[5],offscreenImg;
    Graphics offscreenG;
    public void init(){
        for (int i=0; i<5; i++) {
	    puyoImg[i] = getImage(getDocumentBase(),"puyo"+(i+1)+".gif");
	} 
	addKeyListener (new KeyAdapter(){
                public void keyPressed(KeyEvent e) {
                    int key = e.getKeyCode();
                    int dir = 3;
                    if( key==32 && paint!=1){reset();puyoSet();paint=1;start();}
                    else if(!chigiri && paint==1){
                        switch ( key ) {
                        case 37: dir = 2; break;
                        case 39: dir = 0; break;
                        case 40: dir = 1; break;
                        case 88: dir = -1;break;
                        case 90: dir = -2;break;
                        }
                        if ( dir >= 0 ) puyoMove(dir);
                        else puyoTurn(dir);
                        repaint();
                    }
                }
            });
        offscreenImg = createImage(440,600);
        offscreenG = offscreenImg.getGraphics();
    }
    public void reset() {
	hyouji=0;
	map = new char[MY+2][MX+2];
	flag = new int[MY+2][MX+2];
	for (int x = 0; x <= MX+1; x++) {
	    map[MY+1][x] = 'B';
	}
	for (int y = 0; y <= MY+1; y++) {
	    map[y][0] = 'B';
	    map[y][MX+1] = 'B';
	}
    }
    public void start() {
	if (paint == 1) {
	    th = new Thread(this);
	    th.start();
	}
    }
    public void stop() {
	if (th != null) {
	    th = null;
	}
    }
    public void run() {
	while (th != null) {
	    if(chigiri)falling();
	    else fall();
	}
    }
    public void fall(){
	puyoMove(1);
	repaint();
	sleep(1000);
    }
    public void falling(){
	int fall_count=0;
	for (int y = MY; y >= 1; y--) {
	    for (int x = 1; x <= MX; x++) {
		if ( map[y][x] != '\u0000'&& map[y+1][x]=='\u0000') {
		    map[y+1][x] =map[y][x];
		    map[y][x]='\u0000';
		    fall_count+=1;
		}
	    }
	}
	repaint();
	sleep(100);
	if(fall_count==0){
	    vanish();
	}
	if(vani){
	    vani=false;
	}
    }
    
    public void sleep(int time){
	try { Thread.sleep(time); }
	catch (InterruptedException e) {
	    return;
	}
    }
    
    public void puyoSet() {
	if(map[1][3]!='\u0000'){
	    paint=2;
	    repaint();
	    stop();
	}else{
	    nc1=random1;
	    nc2=random2;
	    random1=rnd.nextInt(4);
	    random2=rnd.nextInt(4);
	    p1_x = 3;
	    p1_y = 1;
	    p2_x = 3;
	    p2_y = 0;
	}
    }
    public void puyoTurn(int dir) {
	int dx2 = 0, dy2 = 0,dx1 = 0, dy1 = 0;
	if(p1_x>p2_x){
	    dx2 = 1;
	    if(dir==-1)dy2 = -1;
	    else dy2 = 1;
	}
	else if(p1_y>p2_y){
	    dy2 = 1;
	    if(dir==-1)dx2 = 1;
	    else dx2 = -1;
	}
	else if(p1_x<p2_x){
	    dx2 = -1; 
	    if(dir==-1)dy2= 1;
	    else dy2 = -1;
	}
	else{
	    dy2 = -1;
	    if(dir==-1)dx2 = -1;
	    else dx2 = 1;
	}
	if(map[p2_y+dy2][p2_x+dx2]!='\u0000'){
	    if(p1_x>p2_x+dx2){
		dx1+=1;dx2+=1;
	    }else if(p1_x<p2_x+dx2){
		dx1-=1;dx2-=1;
	    }else{
		dy1-=1;dy2-=1;
	    }
	    if(map[p1_y+dy1][p1_x+dx1]!='\u0000'){
		dy1=p2_y-p1_y;
		dy2=p1_y-p2_y;
		dx1=0;dx2=0;
	    }
	}
	p1_x+=dx1;
	p1_y+=dy1;
	p2_x+=dx2;
	p2_y+=dy2;
    }
    public void puyoMove(int dir) {
	int dx = 0, dy = 0;
	switch ( dir ) {
	case 0: dx =  1; break;
	case 2: dx = -1; break;
	case 1: dy =  1;if(map[p1_y+dy][p1_x]!='\u0000'||map[p2_y+dy][p2_x]!='\u0000'){puyoFell();chigiri=true;th.interrupt();dy=0;} break;
	}
	if ( dx == 0 && dy == 0 ) return;
	if ( map[p1_y+dy][p1_x+dx] !='\u0000'|| map[p2_y+dy][p2_x+dx] !='\u0000' ) return;
	p1_x += dx; p1_y += dy;p2_x += dx; p2_y += dy;
    }
    public void puyoDraw(Graphics g) {
	if(p1_y!=0){
	    g.drawImage(puyoImg[nc1],p1_x*40+20,p1_y*40+20,this);  
	}
	if(p2_y!=0){
	    g.drawImage(puyoImg[nc2],p2_x*40+20,p2_y*40+20,this); 
	}
    }
    public void puyoFell(){
	switch (nc1) {
	case 0:map[p1_y][p1_x]='r';break;
	case 1:map[p1_y][p1_x]='y';break;
	case 2:map[p1_y][p1_x]='b';break;
	case 3:map[p1_y][p1_x]='g';break;
	}
	switch (nc2) {
	case 0:map[p2_y][p2_x]='r';break;
	case 1:map[p2_y][p2_x]='y';break;
	case 2:map[p2_y][p2_x]='b';break;
	case 3:map[p2_y][p2_x]='g';break;
	}
	p1_y=0;p2_y=0;
    }
    public void vanish(){
	for (int y = 1; y <= MY; y++) {
	    for (int x = 1; x <= MX; x++) {
		count=0;
		judge(x,y);
		for (int k = 1; k <= MY; k++) {
		    for (int j = 1; j <= MX; j++) {
			if ( flag[k][j] == 1)flag[k][j] =0;
		    }
		}
	    }
	}
	for (int y = 1; y <= MY; y++) {
	    for (int x = 1; x <= MX; x++) {
		if(flag[y][x]==2){
		    map[y][x]='v';
		    vani=true;
		}
	    }
	}
	if(!vani){
	    chigiri=false;
	    puyoSet();
	    rensa=0;
	}else{
	    sleep(500);
	    repaint();
	    sleep(500);
	    for (int y = 1; y <= MY; y++) {
		for (int x = 1; x <= MX; x++) {
		    if(flag[y][x]==2){
			map[y][x]='\u0000';
		    }
		    flag[y][x]=0;
		}
	    }
	    repaint();
	    rensa++;
	    if(hyouji<rensa){
		hyouji=rensa;
	    }
	}
    }
    public void judge(int x,int y){
	if(flag[y][x]!=0||map[y][x]=='\u0000')return;
	flag[y][x]=1;
	count+=1;
	if ( map[y][x+1] == map[y][x]&& flag[y][x+1]==0)judge(x+1,y);
	if ( map[y+1][x] == map[y][x]&& flag[y+1][x]==0)judge(x,y+1);
	if ( map[y][x-1] == map[y][x]&& flag[y][x-1]==0)judge(x-1,y);
	if ( y-1>=0 && map[y-1][x] == map[y][x]&& flag[y-1][x]==0)judge(x,y-1);
	if(count>=4){
	    for (int k = 1; k <= MY; k++) {
		for (int j = 1; j <= MX; j++) {
		    if ( flag[k][j] == 1)flag[k][j] =2;
		}
	    }
	}
    }
    public void paint(Graphics g) {
	if(paint==0)g.drawImage(getImage(getDocumentBase(),"start.gif"),0,0,this); 
	else if(paint==2)g.drawImage(getImage(getDocumentBase(),"gameover.gif"),0,0,this); 
	else{
	    setBackground(Color.white);
            offscreenG.setColor(getBackground());
            offscreenG.fillRect(0, 0, 440, 600);
            offscreenG.setColor(getForeground());
            for (int y = 1; y <= MY+1; y++) {
		for (int x = 0; x <= MX+1; x++) {
		    int xx = 40*x+20, yy = 40*y+20;
		    switch ( map[y][x] ) {
		    case 'r':
			offscreenG.drawImage(puyoImg[0],xx,yy,this);  
			break;
		    case 'y':
			offscreenG.drawImage(puyoImg[1],xx,yy,this);
			break;
		    case 'b':
			offscreenG.drawImage(puyoImg[2],xx,yy,this);
			break;
		    case 'g':
			offscreenG.drawImage(puyoImg[3],xx,yy,this);
			break;
		    case 'v':
			offscreenG.drawImage(puyoImg[4],xx,yy,this);
			break;
		    case 'B': offscreenG.setColor(Color.red);
			offscreenG.fillRect(xx, yy, 26, 10);
			offscreenG.fillRect(xx+32, yy, 8, 10);
			offscreenG.fillRect(xx, yy+15, 10, 10);
			offscreenG.fillRect(xx+16, yy+15, 24, 10);
			offscreenG.fillRect(xx, yy+30, 18, 10);
			offscreenG.fillRect(xx+24, yy+30, 16, 10);
			break;
		    }
		}
	    }
	    offscreenG.setFont(new Font("TimeRoman", Font.BOLD, 18));
	    offscreenG.drawString(hyouji+"連鎖",380,120);  
	    offscreenG.drawImage(puyoImg[random1],380,60,this);  
	    offscreenG.drawImage(puyoImg[random2],380,20,this);  
	    puyoDraw(offscreenG);
            g.drawImage(offscreenImg, 0, 0, this);    
        }
    }
    public void update(Graphics g) {
        paint(g);
    }

}
