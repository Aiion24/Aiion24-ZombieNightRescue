package com.zombienight.rescue;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import java.util.*;

public class GameView extends View {
  Paint p = new Paint(); Random rng = new Random(7); long last;
  float sx=1, sy=1, px=220, py=440, vx=0, vy=0; int hp=100; int scene=0; int weapon=0; int score=0; boolean attacking=false;
  ArrayList<Zombie> zs=new ArrayList<>(); ArrayList<Item> items=new ArrayList<>();
  int W,H;
  int[] touchIds=new int[10]; float joyX,joyY; boolean left=false,right=false,up=false,down=false;
  Bitmap ref;
  public GameView(Context c){ super(c); p.setAntiAlias(false); ref=BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("protagonist_reference","drawable",c.getPackageName())); last=System.currentTimeMillis(); setFocusable(true); }
  void col(int c){p.setColor(c);} void rect(Canvas c,float l,float t,float r,float b,int color){col(color);c.drawRect(l,t,r,b,p);} void txt(Canvas c,String s,float x,float y,float size,int color){p.setTypeface(Typeface.create("monospace",Typeface.BOLD));p.setTextSize(size);p.setColor(color);c.drawText(s,x,y,p);}
  @Override protected void onSizeChanged(int w,int h,int ow,int oh){W=w;H=h;sx=W/1280f;sy=H/720f;}
  float X(float x){return x*sx;} float Y(float y){return y*sy;}
  @Override protected void onDraw(Canvas real){ super.onDraw(real); Canvas c=real; c.save(); c.scale(sx,sy); if(scene==0) title(c); else if(scene==1) apartment(c); else city(c); c.restore(); long now=System.currentTimeMillis(); float dt=Math.min(.05f,(now-last)/1000f); last=now; if(scene>=2) update(dt); invalidate(); }
  void title(Canvas c){
    rect(c,0,0,1280,720,0xff07111f); for(int i=0;i<40;i++){float x=(i*173)%1280,y=90+(i*71)%500;rect(c,x,y,x+5,y+5,0xff163d67);}
    txt(c,"ZOMBIE NIGHT",110,160,72,0xfff5f7ff); txt(c,"RESCUE",113,230,86,0xffff4162); txt(c,"PIXEL SURVIVAL • ANDROID",116,275,24,0xff8fb8d9);
    drawPlayer(c,700,360,1,0); txt(c,"UN DÍA NORMAL...",760,355,28,0xffdbeaff); txt(c,"...SE CONVIRTIÓ EN UNA PESADILLA",760,395,22,0xff7d9bb8);
    button(c,430,535,850,620,"COMENZAR",0xffd52f52);
  }
  void apartment(Canvas c){
    rect(c,0,0,1280,720,0xff181927); rect(c,0,0,1280,70,0xff101322); txt(c,"CAPÍTULO 1 — UN DÍA NORMAL",30,45,25,0xffe9efff); txt(c,"LUNES 7:30 AM",1010,45,22,0xffb6c9df);
    rect(c,0,70,1280,520,0xff3d3a49); rect(c,60,100,1220,500,0xff2b2a36); rect(c,560,115,1110,390,0xff172b48); rect(c,580,135,1090,370,0xff274c78);
    for(int i=0;i<8;i++){rect(c,600+i*58,150+(i%2)*25,610+i*58,160+(i%2)*25,0xffffd36e);}
    rect(c,0,500,1280,720,0xff392b2b); rect(c,80,520,440,555,0xff6e4638); rect(c,500,540,930,565,0xff5a3b31); rect(c,970,510,1180,565,0xff5a3b31);
    drawPlayer(c,350,400,1,0); txt(c,"Miras por la ventana...",80,630,30,0xffeef3ff); txt(c,"Algo no está bien.",80,668,22,0xffff7184);
    button(c,900,610,1180,680,"SALIR",0xff2d6fd6);
  }
  void city(Canvas c){
    rect(c,0,0,1280,720,0xff081326);
    for(int i=0;i<18;i++){float x=i*76; float bh=130+(i*37)%280; rect(c,x,520-bh,x+58,520,0xff102743); for(int w=0;w<3;w++) for(int q=0;q<4;q++) if((i+w+q)%2==0) rect(c,x+10+w*16,545-bh+q*32,x+18+w*16,553-bh+q*32,0xfff0c96a);}
    rect(c,0,520,1280,720,0xff252833); for(int i=0;i<18;i++) rect(c,i*75,620+(i%2)*3,i*75+50,625+(i%2)*3,0xff404452);
    rect(c,30,35,350,95,0xff0e1b30); txt(c,"VIDA",50,72,20,0xffa9c2df); rect(c,120,55,320,76,0xff4b1825); rect(c,120,55,120+2*hp,76,0xffef3856); txt(c,"ARMA: "+weaponName(),40,125,20,0xffeaf1ff); txt(c,"PUNTOS "+score,1060,55,22,0xffffd36b);
    drawPlayer(c,px,py,1,weapon);
    for(Zombie z:zs) drawZombie(c,z.x,z.y,z.type);
    for(Item it:items) drawItem(c,it.x,it.y,it.type);
    controls(c);
    if(zs.size()==0 && score>0){txt(c,"¡ZONA DESPEJADA!",500,170,34,0xffffd36b);}
  }
  void update(float dt){
    float dx=(right?1:0)-(left?1:0), dy=(down?1:0)-(up?1:0); float len=(float)Math.hypot(dx,dy); if(len>0){dx/=len;dy/=len;px+=dx*180*dt;py+=dy*180*dt;}
    px=Math.max(80,Math.min(1200,px)); py=Math.max(260,Math.min(545,py));
    for(Zombie z:zs){float dxz=px-z.x,dyz=py-z.y,d=(float)Math.hypot(dxz,dyz); if(d>2){z.x+=dxz/d*z.speed*dt;z.y+=dyz/d*z.speed*dt;} if(d<55){z.hitTimer-=dt;if(z.hitTimer<=0){hp-=z.type==2?12:5;z.hitTimer=0.7f; if(hp<=0){hp=100;px=220;py=440;score=0;zs.clear();items.clear();spawn();}}}}
    if(attacking){for(Zombie z:zs){if(Math.hypot(px-z.x,py-z.y)<95){z.hp-=weapon==2?30:18;z.hitTimer=.4f;}} attacking=false;}
    for(Item it:items){if(Math.hypot(px-it.x,py-it.y)<50 && weapon==0){weapon=it.type;}}
    zs.removeIf(z->{if(z.hp<=0){score+=10;return true;}return false;});
    if(zs.size()<4 && rng.nextFloat()<dt*0.7f) zs.add(new Zombie(1050+rng.nextInt(150),300+rng.nextInt(200),rng.nextInt(3)));
  }
  void spawn(){items.add(new Item(470,450,1));items.add(new Item(720,430,2));items.add(new Item(950,470,3));for(int i=0;i<3;i++)zs.add(new Zombie(800+i*120,330+i*50,i%3));}
  String weaponName(){return weapon==0?"PUÑOS":weapon==1?"BATE":weapon==2?"PISTOLA":"BOTELLA";}
  void drawPlayer(Canvas c,float x,float y,float scale,int we){
    // blocky pixel sprite inspired by supplied character: brown hair, glasses, beard, open white shirt, tattoo, blue jeans
    float s=scale; rect(c,x-28*s,y-115*s,x+28*s,y-55*s,0xfff2b07a); rect(c,x-24*s,y-140*s,x+25*s,y-112*s,0xff6a321e); rect(c,x-30*s,y-132*s,x+30*s,y-122*s,0xff512817);
    rect(c,x-25*s,y-105*s,x-8*s,y-95*s,0xff17171b);rect(c,x+8*s,y-105*s,x+25*s,y-95*s,0xff17171b);rect(c,x-4*s,y-101*s,x+4*s,y-96*s,0xff17171b);rect(c,x-20*s,y-88*s,x+20*s,y-82*s,0xff56301f);
    rect(c,x-58*s,y-55*s,x+58*s,y+55*s,0xffe8e3df); rect(c,x-25*s,y-55*s,x+25*s,y+48*s,0xffa75a32); rect(c,x-6*s,y-30*s,x+7*s,y+22*s,0xff202025); // tattoo
    rect(c,x-50*s,y-40*s,x-34*s,y+48*s,0xfff0b27b);rect(c,x+34*s,y-40*s,x+50*s,y+48*s,0xfff0b27b);
    rect(c,x-36*s,y+42*s,x-4*s,y+125*s,0xff2e5684);rect(c,x+4*s,y+42*s,x+36*s,y+125*s,0xff2e5684);
    rect(c,x-40*s,y+115*s,x-3*s,y+128*s,0xfff1e6e0);rect(c,x+3*s,y+115*s,x+42*s,y+128*s,0xfff1e6e0);
    if(we==1){rect(c,x+38*s,y-5*s,x+100*s,y+7*s,0xffa96a37);rect(c,x+94*s,y-1*s,x+106*s,y+11*s,0xffe3b16d);} else if(we==2){rect(c,x+42*s,y-15*s,x+83*s,y+2*s,0xff2c2d34);rect(c,x+74*s,y-21*s,x+87*s,y-12*s,0xff15161a);} else if(we==3){rect(c,x+45*s,y-15*s,x+68*s,y+8*s,0xff5e9c54);rect(c,x+52*s,y-28*s,x+60*s,y-16*s,0xff7fd26f);}
  }
  void drawZombie(Canvas c,float x,float y,int t){int body=t==2?0xff7c7f83:t==1?0xff8a9b70:0xff667d63;float s=t==2?1.35f:t==1?0.9f:1;rect(c,x-25*s,y-70*s,x+25*s,y+40*s,body);rect(c,x-22*s,y-105*s,x+22*s,y-68*s,0xff9b6b61);rect(c,x-18*s,y-98*s,x-5*s,y-88*s,0xff211b1d);rect(c,x+5*s,y-98*s,x+18*s,y-88*s,0xff211b1d);rect(c,x-45*s,y-40*s,x-25*s,y-25*s,body);rect(c,x+25*s,y-40*s,x+45*s,y-25*s,body);rect(c,x-20*s,y+38*s,x-5*s,y+75*s,body);rect(c,x+5*s,y+38*s,x+20*s,y+75*s,body);}
  void drawItem(Canvas c,float x,float y,int t){if(t==1){rect(c,x-8,y-35,x+8,y+35,0xffa76b3e);rect(c,x-13,y-5,x+13,y+4,0xffd9a15f);} else if(t==2){rect(c,x-20,y-8,x+20,y+10,0xff2d3037);rect(c,x+10,y-14,x+24,y-7,0xff17191e);} else {rect(c,x-12,y-20,x+12,y+22,0xff4d9b56);rect(c,x-5,y-27,x+5,y-20,0xff77bf75);}}
  void controls(Canvas c){
    col(0x553a4c68);c.drawCircle(110,610,82,p);c.drawCircle(110,610,34,p);txt(c,"▲",98,552,30,0xffdbe8f6);txt(c,"▼",98,686,30,0xffdbe8f6);txt(c,"◀",43,618,30,0xffdbe8f6);txt(c,"▶",163,618,30,0xffdbe8f6);
    button(c,1030,560,1135,665,"ATACAR",0xffb52e4c);button(c,1150,500,1250,595,"USAR",0xff2f6fb4);
  }
  void button(Canvas c,float l,float t,float r,float b,String s,int color){rect(c,l,t,r,b,0xff111a2b);rect(c,l+4,t+4,r-4,b-4,color);float tw=p.measureText(s);txt(c,s,(l+r)/2-s.length()*7,t+(b-t)/2+8,22,Color.WHITE);}
  void drawScreenText(Canvas c,String s,float x,float y){txt(c,s,x,y,22,Color.WHITE);}
  @Override public boolean onTouchEvent(android.view.MotionEvent e){float x=e.getX()/sx,y=e.getY()/sy; int a=e.getActionMasked();
    if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_POINTER_DOWN){if(scene==0&&x>400&&x<880&&y>500){scene=1;return true;} if(scene==1&&x>850&&y>580){scene=2;spawn();return true;} if(scene>=2){if(x<260&&y>520){joyX=x;joyY=y;return true;} if(x>1000&&y>520){attacking=true;return true;} if(x>1120&&y>450){weapon=0;return true;}}}
    if(scene>=2&&(a==MotionEvent.ACTION_MOVE||a==MotionEvent.ACTION_DOWN)){left=x<85&&y>550;right=x>135&&x<260&&y>550;up=x>70&&x<160&&y<570;down=x>70&&x<160&&y>650;}
    if(a==MotionEvent.ACTION_UP||a==MotionEvent.ACTION_CANCEL){left=right=up=down=false;}
    return true; }
  static class Zombie{float x,y,speed,hp,hitTimer;int type;Zombie(float x,float y,int t){this.x=x;this.y=y;type=t;speed=t==0?55:t==1?85:40;hp=t==2?80:35;hitTimer=0;}}
  static class Item{float x,y;int type;Item(float x,float y,int t){this.x=x;this.y=y;type=t;}}
}
