package com.zombienight.rescue;

import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.*;

/**
 * Zombie Night: Rescue - detailed procedural pixel-art game view.
 * Visual direction follows the supplied reference: dark blue UI, warm interiors,
 * chunky pixel characters, cinematic lighting and a touch-first HUD.
 */
public class GameView extends View {
  final Paint p = new Paint();
  final Random rng = new Random(42);
  long last;
  int W,H; float sx=1,sy=1;
  int scene=0; // 0 title, 1 apartment, 2 city, 3 prison
  int hp=100, score=0, weapon=0, wave=0;
  float px=280, py=455;
  boolean moveL,moveR,moveU,moveD,attack,interact;
  float attackTimer=0, messageTimer=0;
  String message="";
  ArrayList<Zombie> enemies=new ArrayList<>();
  ArrayList<Item> items=new ArrayList<>();

  public GameView(Context c){
    super(c); p.setAntiAlias(false); p.setDither(false); setFocusable(true);
    last=System.currentTimeMillis();
  }
  void color(int c){p.setColor(c);}
  void rect(Canvas c,float l,float t,float r,float b,int col){color(col);c.drawRect(l,t,r,b,p);}
  void line(Canvas c,float x1,float y1,float x2,float y2,int col,float sw){color(col);p.setStrokeWidth(sw);c.drawLine(x1,y1,x2,y2,p);}
  void text(Canvas c,String s,float x,float y,float size,int col){p.setTypeface(Typeface.create("monospace",Typeface.BOLD));p.setTextSize(size);p.setColor(col);c.drawText(s,x,y,p);}
  void panel(Canvas c,float l,float t,float r,float b,int fill,int edge){rect(c,l,t,r,b,fill);line(c,l,t,r,t,edge,3);line(c,l,b,r,b,edge,3);line(c,l,t,l,b,edge,3);line(c,r,t,r,b,edge,3);}
  @Override protected void onSizeChanged(int w,int h,int ow,int oh){W=w;H=h;sx=W/1280f;sy=H/720f;}
  @Override protected void onDraw(Canvas real){
    super.onDraw(real); real.save();real.scale(sx,sy);
    if(scene==0) title(real); else if(scene==1) apartment(real); else if(scene==2) city(real); else prison(real);
    real.restore();
    long now=System.currentTimeMillis();float dt=Math.min(.045f,(now-last)/1000f);last=now;
    if(scene>=2) update(dt); invalidate();
  }

  void title(Canvas c){
    rect(c,0,0,1280,720,0xff050b16);
    // atmospheric pixel stars / rain
    for(int i=0;i<80;i++){float x=(i*97)%1280,y=(i*43)%430+30;rect(c,x,y,x+3,y+3,(i%5==0)?0xff6f9ac0:0xff18304e);}
    rect(c,0,475,1280,720,0xff0a1320);
    // skyline
    for(int i=0;i<18;i++){float x=i*76,b=120+(i*31)%180;rect(c,x,475-b,x+60,475,0xff0d1d32);for(int q=0;q<4;q++)if((i+q)%2==0)rect(c,x+10,300-b+q*32,x+17,307-b+q*32,0xffd7a75b);}
    text(c,"ZOMBIE NIGHT",86,160,76,0xfff0f5ff); text(c,"RESCUE",90,240,92,0xffff3e62);
    text(c,"NO ES SOLO UNA LUCHA CONTRA ZOMBIES...",92,292,23,0xffb8cee5);
    text(c,"ES UNA LUCHA POR LO QUE AMAS.",92,326,23,0xffe4edf8);
    drawPlayer(c,650,385,1.35f,0,true);
    // title HUD mockup
    panel(c,770,320,1180,505,0xcc091426,0xff244c75);
    text(c,"PRIMER ESCENARIO",805,360,23,0xff7fc4ff);
    text(c,"UN DÍA NORMAL",805,405,35,0xffffffff);
    text(c,"EN CASA",805,447,30,0xffffc85a);
    button(c,420,565,860,655,"COMENZAR",0xffbd2f50);
  }

  void apartment(Canvas c){
    rect(c,0,0,1280,720,0xff171522);
    // warm apartment wall / floor
    rect(c,0,70,1280,505,0xff4a4653); rect(c,0,505,1280,720,0xff30262a);
    // window and sunset city
    panel(c,505,115,1115,400,0xff1b2334,0xff121a2a);rect(c,530,140,1090,375,0xffd58d76);
    rect(c,530,250,1090,375,0xff3b4966);
    for(int i=0;i<13;i++){float bx=545+i*42,bh=50+(i*17)%115;rect(c,bx,375-bh,bx+31,375,0xff283246);for(int q=0;q<4;q++)if((i+q)%3!=0)rect(c,bx+7,295-bh+q*24,bx+13,301-bh+q*24,0xffffcf76);}
    // furniture
    rect(c,70,475,390,515,0xff654338);rect(c,95,430,365,480,0xff805444);rect(c,120,455,340,490,0xffa96d51);
    rect(c,470,515,875,550,0xff5b3c31);rect(c,515,550,545,635,0xff39262a);rect(c,805,550,835,635,0xff39262a);
    rect(c,1030,435,1200,520,0xff31303a);rect(c,1050,420,1180,450,0xff22222b);rect(c,1090,380,1150,420,0xff17171d);
    // lamps and decor
    rect(c,150,170,157,330,0xff27222a);rect(c,125,155,182,185,0xffffd58b);rect(c,130,185,178,195,0xffc08a58);
    panel(c,105,95,300,300,0xff282331,0xff12131c);rect(c,125,115,280,280,0xffa14d42);rect(c,150,135,260,235,0xff172538);
    text(c,"CAPÍTULO 1 — UN DÍA NORMAL",30,40,25,0xffedf4ff);text(c,"LUNES 7:30 AM",1010,40,22,0xffbcd0e6);
    drawPlayer(c,350,430,1.0f,0,true);
    panel(c,45,595,735,680,0xcc0a1424,0xff234b72);
    text(c,"Todo parece normal...",75,630,27,0xffdbe9f6);text(c,"Pero algo no está bien.",75,662,23,0xffff6f83);
    button(c,930,590,1190,675,"SALIR",0xff2d6db2);
  }

  void city(Canvas c){
    rect(c,0,0,1280,720,0xff07101e);
    // moon glow
    color(0xff253452);c.drawCircle(1040,115,62,p);color(0xffb9c9d9);c.drawCircle(1040,115,38,p);
    // dense city silhouettes
    for(int i=0;i<22;i++){float x=i*61-20,bh=120+(i*53)%260;rect(c,x,505-bh,x+48,505,0xff0d2138);for(int q=0;q<6;q++)if((i+q)%3!=1)rect(c,x+8+(q%2)*18,300-bh+q*34,x+14+(q%2)*18,306-bh+q*34,0xffd4a45b);}
    // street
    rect(c,0,505,1280,720,0xff242a34);line(c,0,505,1280,505,0xff516071,3);
    for(int i=0;i<10;i++)rect(c,90+i*135,625,165+i*135,633,0xff7c6d55);
    // convenience store
    panel(c,45,300,360,510,0xff151d29,0xff426a83);rect(c,65,325,340,390,0xff1b5260);text(c,"24",78,365,38,0xffff5b5f);text(c,"CONVENIENCE",142,360,20,0xffe8f2df);for(int i=0;i<6;i++)rect(c,78+i*40,405,108+i*40,470,0xff273746);
    // street lamps
    for(int i=0;i<5;i++){float x=420+i*190;rect(c,x,250,x+7,510,0xff242832);rect(c,x-18,240,x+25,252,0xff323845);color(0xffd3a65f);c.drawCircle(x+4,260,12,p);}
    // enemies
    for(Zombie z:enemies) drawZombie(c,z.x,z.y,z.type,z.boss);
    // player
    drawPlayer(c,px,py,1.0f,weapon,true);
    hud(c);
    if(messageTimer>0){panel(c,420,90,1160,165,0xdd081525,0xff3e6b92);text(c,message,455,135,23,0xffeef6ff);}
    // mission card
    panel(c,840,15,1250,82,0xdd081322,0xff315a7e);text(c,scene==2?"MISIÓN: SOBREVIVE":"",860,48,20,0xffffcf67);
  }

  void prison(Canvas c){
    rect(c,0,0,1280,720,0xff090c14);rect(c,0,0,1280,70,0xff111826);
    text(c,"CAPÍTULO 3 — LA PRISIÓN",35,45,27,0xfff0f4ff);text(c,"OBJETIVO: ENCUENTRA A TU NOVIA",760,45,21,0xffffc85a);
    // corridor
    rect(c,100,110,1180,700,0xff292c34);for(int i=0;i<9;i++){float x=130+i*130;line(c,x,120,x+100,700,0xff171a21,4);}line(c,100,150,1180,150,0xff555966,4);
    // cell doors
    for(int i=0;i<5;i++){float x=180+i*190;panel(c,x,205,x+135,570,0xff151921,0xff5b5f6a);for(int q=0;q<6;q++)line(c,x+20+q*18,220,x+20+q*18,555,0xff383c45,4);rect(c,x+48,380,x+87,390,0xffc09656);}
    // girlfriend silhouette behind cell
    drawPerson(c,815,420);
    drawPlayer(c,390,505,1.0f,weapon,true);
    panel(c,690,95,1190,175,0xdd0a1320,0xff4a6d8d);text(c,"EL CEREBRO: 'VINISTE POR ELLA.'",720,140,22,0xffff7180);
    hud(c);
  }

  void hud(Canvas c){
    panel(c,20,15,335,88,0xdd081321,0xff315575);text(c,"VIDA",40,45,18,0xffd6e4f2);rect(c,100,30,320,58,0xff4c1c2a);rect(c,100,30,100+2.2f*hp,58,0xffef3b58);text(c,weaponName(),40,78,17,0xffffc966);text(c,"PUNTOS "+score,1090,48,20,0xffe8f0ff);
    // joystick
    color(0x553d5069);c.drawCircle(105,615,78,p);color(0x886f8ba5);c.drawCircle(105,615,31,p);text(c,"▲",95,565,25,Color.WHITE);text(c,"▼",95,678,25,Color.WHITE);text(c,"◀",43,623,25,Color.WHITE);text(c,"▶",157,623,25,Color.WHITE);
    button(c,1010,555,1125,670,"ATACAR",0xffad2e4d);button(c,1140,480,1250,580,"USAR",0xff2d6fb2);
  }

  void update(float dt){
    if(messageTimer>0)messageTimer-=dt;
    if(scene==2){
      float dx=(moveR?1:0)-(moveL?1:0),dy=(moveD?1:0)-(moveU?1:0);float len=(float)Math.hypot(dx,dy);if(len>0){dx/=len;dy/=len;px+=dx*185*dt;py+=dy*185*dt;}
      px=Math.max(90,Math.min(1190,px));py=Math.max(250,Math.min(575,py));
      if(attackTimer>0)attackTimer-=dt;
      if(attack){attack=false;attackTimer=.22f;for(Zombie z:enemies){float d=(float)Math.hypot(px-z.x,py-z.y);if(d<115){z.hp-=weapon==2?55:weapon==1?35:24;z.stun=.35f;}}}
      for(Item it:items){if(Math.hypot(px-it.x,py-it.y)<55){weapon=it.type;show("Has recogido: "+weaponName());it.x=-999;}}
      for(Zombie z:enemies){if(z.stun>0){z.stun-=dt;continue;}float dxz=px-z.x,dyz=py-z.y,d=(float)Math.hypot(dxz,dyz);if(d>2){z.x+=dxz/d*z.speed*dt;z.y+=dyz/d*z.speed*dt;}if(d<62){z.hit-=dt;if(z.hit<=0){hp-=z.boss?15:(z.type==2?10:5);z.hit=.65f;}}}
      for(int i=enemies.size()-1;i>=0;i--){Zombie z=enemies.get(i);if(z.hp<=0){score+=z.boss?150:10;enemies.remove(i);}}
      if(enemies.size()<5 && rng.nextFloat()<dt*.55f)spawnEnemy();
      if(score>=300 && wave<2){wave=2;bossWave();show("¡UN JEFE HA APARECIDO!");}
      if(score>=650 && wave<3){wave=3;scene=3;enemies.clear();show("EL CEREBRO: Te espero en la prisión...");}
    }
  }

  void spawnEnemy(){int t=rng.nextInt(3);enemies.add(new Zombie(1000+rng.nextInt(180),300+rng.nextInt(230),t,false));}
  void bossWave(){enemies.add(new Zombie(1040,400,2,true));}
  void startCity(){scene=2;px=280;py=455;hp=100;score=0;weapon=0;wave=1;enemies.clear();items.clear();items.add(new Item(470,470,1));items.add(new Item(700,420,2));items.add(new Item(900,500,3));for(int i=0;i<4;i++)spawnEnemy();show("La ciudad se ha convertido en una pesadilla.");}
  void show(String s){message=s;messageTimer=3.0f;}
  String weaponName(){return weapon==0?"PUÑOS":weapon==1?"BATE":weapon==2?"PISTOLA":weapon==3?"BOTELLA":"ESPADA";}

  void drawPlayer(Canvas c,float x,float y,float s,int we,boolean shadow){
    if(shadow){color(0x66000000);c.drawOval(x-48*s,y+108*s,x+48*s,y+124*s,p);}
    // chunky pixel-art body, glasses, beard, open white shirt and blue jeans
    rect(c,x-27*s,y-132*s,x+27*s,y-82*s,0xfff1ad78); // face
    rect(c,x-28*s,y-154*s,x+29*s,y-128*s,0xff6b321f);rect(c,x-34*s,y-145*s,x+34*s,y-136*s,0xff482216);
    rect(c,x-25*s,y-118*s,x-5*s,y-108*s,0xff17181b);rect(c,x+5*s,y-118*s,x+25*s,y-108*s,0xff17181b);rect(c,x-5*s,y-114*s,x+5*s,y-109*s,0xff1a1b20);rect(c,x-18*s,y-98*s,x+18*s,y-89*s,0xff543022);
    rect(c,x-55*s,y-82*s,x+55*s,y+45*s,0xffece9e4);rect(c,x-23*s,y-82*s,x+23*s,y+45*s,0xffa45a34);rect(c,x-5*s,y-70*s,x+7*s,y+20*s,0xff242229);
    // tattoo pixels
    for(int i=0;i<5;i++)rect(c,x-18*s+i*7,y-20*s,x-13*s+i*7,y-14*s,0xff292329);
    rect(c,x-47*s,y-64*s,x-32*s,y+50*s,0xffefad77);rect(c,x+32*s,y-64*s,x+47*s,y+50*s,0xffefad77);
    rect(c,x-34*s,y+38*s,x-4*s,y+126*s,0xff2f5888);rect(c,x+4*s,y+38*s,x+34*s,y+126*s,0xff2f5888);
    rect(c,x-39*s,y+115*s,x-3*s,y+130*s,0xfff2e9e3);rect(c,x+3*s,y+115*s,x+41*s,y+130*s,0xfff2e9e3);
    // weapon pixels
    if(we==1){rect(c,x+38*s,y-5*s,x+105*s,y+9*s,0xff9c6239);rect(c,x+96*s,y-1*s,x+111*s,y+13*s,0xffd29b5d);}
    if(we==2){rect(c,x+42*s,y-18*s,x+87*s,y-3*s,0xff272a31);rect(c,x+76*s,y-25*s,x+91*s,y-14*s,0xff111318);}
    if(we==3){rect(c,x+42*s,y-17*s,x+66*s,y+10*s,0xff4e9654);rect(c,x+49*s,y-27*s,x+59*s,y-17*s,0xff76bd72);}
    if(we==4){rect(c,x+39*s,y-10*s,x+102*s,y-5*s,0xffc6ccd5);rect(c,x+92*s,y-16*s,x+108*s,y-2*s,0xffdbe2ea);}
    if(attackTimer>0){rect(c,x+85*s,y-38*s,x+92*s,y+20*s,0xffffd26d);rect(c,x+95*s,y-25*s,x+101*s,y+7*s,0xffff6f6f);}
  }

  void drawZombie(Canvas c,float x,float y,int t,boolean boss){
    float s=boss?1.65f:(t==2?1.25f:t==1?.9f:1f);int skin=boss?0xff873f43:(t==2?0xff858b8e:(t==1?0xff78906d:0xff667d63));
    color(0x55000000);c.drawOval(x-45*s,y+60*s,x+45*s,y+75*s,p);
    rect(c,x-25*s,y-80*s,x+25*s,y+42*s,skin);rect(c,x-22*s,y-112*s,x+22*s,y-75*s,0xff9c665e);
    rect(c,x-19*s,y-103*s,x-6*s,y-94*s,0xff17171b);rect(c,x+6*s,y-103*s,x+19*s,y-94*s,0xff17171b);
    rect(c,x-47*s,y-52*s,x-25*s,y-36*s,skin);rect(c,x+25*s,y-52*s,x+47*s,y-36*s,skin);rect(c,x-18*s,y+40*s,x-5*s,y+76*s,skin);rect(c,x+5*s,y+40*s,x+18*s,y+76*s,skin);
    if(boss){for(int i=0;i<5;i++)rect(c,x-30*s+i*15,y-130*s,x-24*s+i*15,y-112*s,0xff9e4b38);rect(c,x-17*s,y-66*s,x+17*s,y-57*s,0xff40191d);}
  }
  void drawPerson(Canvas c,float x,float y){rect(c,x-24,y-85,x+24,y+35,0xff4e5664);rect(c,x-20,y-120,x+20,y-84,0xffd09a7b);rect(c,x-24,y-126,x+24,y-116,0xff402b2b);rect(c,x-45,y-55,x-24,y-40,0xffd09a7b);rect(c,x+24,y-55,x+45,y-40,0xffd09a7b);rect(c,x-16,y+35,x-4,y+70,0xff394352);rect(c,x+4,y+35,x+16,y+70,0xff394352);}
  void drawItem(Canvas c,float x,float y,int t){if(x<0)return;if(t==1){rect(c,x-8,y-38,x+8,y+35,0xff9f673c);rect(c,x-13,y-4,x+13,y+5,0xffd4a064);}else if(t==2){rect(c,x-22,y-8,x+22,y+10,0xff30333a);rect(c,x+12,y-16,x+25,y-7,0xff15171c);}else{rect(c,x-12,y-22,x+12,y+22,0xff4b9855);rect(c,x-5,y-30,x+5,y-21,0xff7bc276);}}

  void button(Canvas c,float l,float t,float r,float b,String s,int fill){panel(c,l,t,r,b,0xff0b1422,0xff375a78);rect(c,l+5,t+5,r-5,b-5,fill);text(c,s,(l+r)/2-s.length()*7,t+(b-t)/2+8,23,Color.WHITE);}

  @Override public boolean onTouchEvent(MotionEvent e){
    float x=e.getX()/sx,y=e.getY()/sy;int a=e.getActionMasked();
    if(a==MotionEvent.ACTION_DOWN||a==MotionEvent.ACTION_POINTER_DOWN){
      if(scene==0 && x>390&&x<890&&y>540){scene=1;return true;}
      if(scene==1 && x>880&&y>565){startCity();return true;}
      if(scene>=2){if(x<270&&y>520){joyTouch(x,y);return true;}if(x>985&&y>520){attack=true;return true;}if(x>1120&&y>430){weapon=0;show("Arma guardada");return true;}}
    }
    if(scene>=2 && (a==MotionEvent.ACTION_MOVE||a==MotionEvent.ACTION_DOWN) && x<300&&y>500)joyTouch(x,y);
    if(a==MotionEvent.ACTION_UP||a==MotionEvent.ACTION_CANCEL){moveL=moveR=moveU=moveD=false;}
    return true;
  }
  void joyTouch(float x,float y){moveL=x<75;moveR=x>135;moveU=y<590;moveD=y>645;}

  static class Zombie{float x,y,speed,hp,hit=0,stun=0;int type;boolean boss;Zombie(float x,float y,int t,boolean b){this.x=x;this.y=y;type=t;boss=b;speed=b?42:(t==1?82:t==2?48:58);hp=b?280:(t==2?90:38);}}
  static class Item{float x,y;int type;Item(float x,float y,int t){this.x=x;this.y=y;type=t;}}
}
