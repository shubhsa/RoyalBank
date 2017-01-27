import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.Vector;
import java.text.*;

class TransDet
{
 int trnid;
 String type;
 int amt;
 String status;

 TransDet(int trnid,String type,int amt,String status)
 {
  this.trnid=trnid;
  this.type=type;
  this.amt=amt;
  this.status=status;
 }
}

class BankFrame extends JFrame implements ChangeListener,ActionListener,FocusListener,ListSelectionListener,ItemListener
{
 int bal,selCnt;

 static int actid,trnid,trnCount;

 PreparedStatement insCust,getCust,updCust,insTrans,getTrans,delTrans;

 JTabbedPane jtp;
 
 JTextField tAct1,tNam1,tBal1;
 JButton bSub1,bClr1,bCls1;

 JTextField tTrn2,tAct2,tNam2,tAmt2;
 JButton    bSub2,bClr2,bCls2;
 JRadioButton rDr,rCr;

 JList lst3;
 JTextField tNam3,tBal3,tMsg3,tDet3[];
 JButton bDel3;
 JCheckBox chk3[];
 Vector vct3;
 Vector<TransDet> tVect;

 BankFrame()
 {
  setTitle("Royal Bank");
  setSize(600,400);
  setLocation(300,50);
 
  try
  {
   Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306","USERNAME","PASSWORD");
  
   Statement st=cn.createStatement();
   st.executeUpdate("create database if not exists royalbank");
   st.executeQuery("use royalbank");
   cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/royalbank","root","786420");

   st.executeUpdate("create table if not exists cust (actid int not null,name varchar(45),balance int,primary key(actid))");
   st.executeUpdate("create table if not exists trans(trnid int not null,actid int not null,type varchar(10),dot date,amt int,status varchar(20),primary key(trnid))");


   ResultSet rs=st.executeQuery("select max(actid) as maxid from cust");

   rs.next();
   
   actid=rs.getInt("maxid");
   if(actid==0)
    actid=101;
   else
    actid++;

   rs=st.executeQuery("select max(trnid) as maxid from trans");
   rs.next(); 
   trnid=rs.getInt("maxid");
   if(trnid==0)
    trnid=1001;
   else
    trnid++;

   rs=st.executeQuery("select * from cust");
   vct3=new Vector();
   while(rs.next())
   {
    vct3.add(" "+rs.getInt("actid")+" ");
   }
 
   insCust=cn.prepareStatement("insert into cust value(?,?,?)"); 
   getCust=cn.prepareStatement("select * from cust where actid=?");
   updCust=cn.prepareStatement("update cust set balance=? where actid=?");
   insTrans=cn.prepareStatement("insert into trans value(?,?,?,?,?,?)");  
   getTrans=cn.prepareStatement("select * from trans where actid=?");
   delTrans=cn.prepareStatement("delete from trans where trnid=?");
  }
  catch(SQLException e)
  {
   JOptionPane.showMessageDialog(this,"SQL Alert[1] - "+e.getMessage()); 
  }

  jtp=new JTabbedPane();
  jtp.setFont(new Font("Lucida Console",Font.BOLD,18));
  jtp.setForeground(Color.black);
  BankPanel bp=new BankPanel(20,10,10,10,Color.black);
  bp.setLayout(new BorderLayout(10,10));
  add(bp);
  bp.add(jtp,BorderLayout.CENTER);

  //-------------------NEW ACCOUNT PANEL------------------------//

  BankPanel p1=new BankPanel(40,15,30,15,new Color(60,60,110));  
  
   BankPanel p11=new BankPanel(1,1,1,250,new Color(60,60,110));
  
    JLabel l11=new JLabel("Account ID");
    l11.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l11.setForeground(Color.white);
   
    tAct1=new JTextField(actid+"");
    tAct1.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    tAct1.setForeground(Color.white);
    tAct1.setBackground(Color.red);
    tAct1.setEditable(false);
  
   p11.setLayout(new BorderLayout(45,10));
   p11.add(l11,BorderLayout.WEST);   
   p11.add(tAct1,BorderLayout.CENTER);

   BankPanel p12=new BankPanel(1,1,1,1,new Color(60,60,110));
  
    JLabel l12=new JLabel("Customer Name");
    l12.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l12.setForeground(Color.white);
    
    tNam1=new JTextField();
    tNam1.setFont(new Font("Lucida Console",Font.PLAIN,18));   
  
   p12.setLayout(new BorderLayout(12,10));
   p12.add(l12,BorderLayout.WEST);   
   p12.add(tNam1,BorderLayout.CENTER);

   BankPanel p13=new BankPanel(1,1,1,250,new Color(60,60,110));
  
    JLabel l13=new JLabel("Opening Bal");
    l13.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l13.setForeground(Color.white);
   
    tBal1=new JTextField();
    tBal1.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    tBal1.setForeground(Color.black);
  
   p13.setLayout(new BorderLayout(35,10));
   p13.add(l13,BorderLayout.WEST);   
   p13.add(tBal1,BorderLayout.CENTER);

   BankPanel p14=new BankPanel(1,1,1,1,new Color(60,60,110));   
    
    bSub1=new JButton("Submit");
    bSub1.setBackground(new Color(84,193,127));
    bSub1.setFont(new Font("Lucida Console",Font.BOLD,18));   
    
    bClr1=new JButton("Clear");
    bClr1.setBackground(new Color(153,204,255));
    bClr1.setFont(new Font("Lucida Console",Font.BOLD,18));   

    bCls1=new JButton("Close");
    bCls1.setBackground(new Color(255,80,80));
    bCls1.setFont(new Font("Lucida Console",Font.BOLD,18));   
    
   p14.setLayout(new GridLayout(1,3,15,15));
   p14.add(bSub1);   
   p14.add(bClr1);
   p14.add(bCls1);

  p1.setLayout(new GridLayout(5,1,10,10));
  p1.add(p11);
  p1.add(p12);
  p1.add(p13);
  p1.add(new JLabel());
  p1.add(p14);


  //-------------------TRANSACTION PANEL-------------------------//

  BankPanel p2=new BankPanel(10,10,10,10,new Color(120,80,150));

   BankPanel p21=new BankPanel(8,1,8,250,new Color(120,80,150));
   
    JLabel l21=new JLabel("Trans ID");
    l21.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l21.setForeground(Color.white);
   
    tTrn2=new JTextField(trnid+"");
    tTrn2.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    tTrn2.setForeground(Color.white);
    tTrn2.setBackground(Color.red);
    tTrn2.setEditable(false);
  
   p21.setLayout(new BorderLayout(35,10));
   p21.add(l21,BorderLayout.WEST);   
   p21.add(tTrn2,BorderLayout.CENTER);

  BankPanel p22=new BankPanel(8,1,8,250,new Color(120,80,150));
  
    JLabel l22=new JLabel("Acct ID");
    l22.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l22.setForeground(Color.white);
   
    tAct2=new JTextField();
    tAct2.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    
   p22.setLayout(new BorderLayout(45,10));
   p22.add(l22,BorderLayout.WEST);   
   p22.add(tAct2,BorderLayout.CENTER);

  BankPanel p23=new BankPanel(8,1,8,50,new Color(120,80,150));
  
    JLabel l23=new JLabel("Name");
    l23.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l23.setForeground(Color.white);
   
    tNam2=new JTextField();
    tNam2.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    tNam2.setEditable(false);
    
   p23.setLayout(new BorderLayout(78,10));
   p23.add(l23,BorderLayout.WEST);   
   p23.add(tNam2,BorderLayout.CENTER);

  BankPanel p24=new BankPanel(1,10,1,10,new Color(120,80,150));
  p24.setLayout(new GridLayout(1,2,30,10));

   BankPanel p241=new BankPanel(15,10,10,10,new Color(120,80,150));
   p241.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.white,1),"Transaction Type",
                  TitledBorder.CENTER,TitledBorder.DEFAULT_POSITION,(new Font("Lucida Console",Font.PLAIN,12)),
                  Color.white));
   
   ButtonGroup bg =new ButtonGroup();

   rDr=new JRadioButton("DR",true);
   rDr.setForeground(Color.white);
   rDr.setBackground(new Color(120,80,150));
   rDr.setFont(new Font("Lucida Console",Font.PLAIN,18));   
      
   rCr=new JRadioButton("CR");
   rCr.setForeground(Color.white);
   rCr.setBackground(new Color(120,80,150));
   rCr.setFont(new Font("Lucida Console",Font.PLAIN,18));   
       
   bg.add(rDr);
   bg.add(rCr);
  
   p241.setLayout(new GridLayout(1,2,10,1));
   p241.add(rDr);   
   p241.add(rCr);

   BankPanel p242=new BankPanel(8,1,8,1,new Color(120,80,150));
  
    JLabel l242=new JLabel("Amount");
    l242.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    l242.setForeground(Color.white);
   
    tAmt2=new JTextField();
    tAmt2.setFont(new Font("Lucida Console",Font.PLAIN,18));   
    tAmt2.setForeground(Color.black);
  
    p242.setLayout(new BorderLayout(10,10));
    p242.add(l242,BorderLayout.WEST);   
    p242.add(tAmt2,BorderLayout.CENTER);

   p24.add(p241);   
   p24.add(p242);

  BankPanel p26=new BankPanel(1,1,1,1,new Color(120,80,150));   
    
    bSub2=new JButton("Commit");
    bSub2.setBackground(new Color(84,193,127));
    bSub2.setFont(new Font("Lucida Console",Font.BOLD,18));   
    
    bClr2=new JButton("Clear");
    bClr2.setBackground(new Color(153,204,255));
    bClr2.setFont(new Font("Lucida Console",Font.BOLD,18));   

    bCls2=new JButton("Close");
    bCls2.setBackground(new Color(255,80,80));
    bCls2.setFont(new Font("Lucida Console",Font.BOLD,18));   
    
   p26.setLayout(new GridLayout(1,3,15,15));
   p26.add(bSub2);   
   p26.add(bClr2);
   p26.add(bCls2);

  p2.setLayout(new GridLayout(6,1,10,10));
  p2.add(p21);
  p2.add(p22);
  p2.add(p23);
  p2.add(p24);
  p2.add(new JLabel());
  p2.add(p26);

 
  //---------------------CANCEL PANEL---------------------------//

  BankPanel p3=new BankPanel(10,10,10,10,new Color(60,110,60));

   lst3=new JList(vct3);
   lst3.setFont(new Font("lucida console",Font.PLAIN,18));
   lst3.setForeground(Color.red);
   lst3.setForeground(Color.red);
   lst3.setSelectionBackground(Color.red);
   lst3.setSelectionForeground(Color.white);

   JScrollPane jsp31=new JScrollPane(lst3);
   jsp31.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
   jsp31.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
  
   BankPanel p31=new BankPanel(1,1,1,1,new Color(60,110,60));
   p31.setLayout(new BorderLayout(10,5));

    BankPanel p311=new BankPanel(1,1,1,1,new Color(60,110,60));
    p311.setLayout(new GridLayout(2,1,10,10));
 
     BankPanel p3111=new BankPanel(1,0,1,0,new Color(60,110,60));
     p3111.setLayout(new BorderLayout(42,10));

      JLabel l31=new JLabel("Name");
      l31.setFont(new Font("lucida console",Font.PLAIN,18));
      l31.setForeground(Color.white);

      tNam3=new JTextField();
      tNam3.setFont(new Font("lucida console",Font.PLAIN,18));
      tNam3.setEditable(false);

    p3111.add(l31,BorderLayout.WEST);
    p3111.add(tNam3,BorderLayout.CENTER);

     BankPanel p3112=new BankPanel(1,0,1,230,new Color(60,110,60));
     p3112.setLayout(new BorderLayout(10,10));

      JLabel l32=new JLabel("Balance");
      l32.setFont(new Font("lucida console",Font.PLAIN,18));
      l32.setForeground(Color.white);

      tBal3=new JTextField();
      tBal3.setFont(new Font("lucida console",Font.PLAIN,18));
      tBal3.setEditable(false);

    p3112.add(l32,BorderLayout.WEST);
    p3112.add(tBal3,BorderLayout.CENTER);

   p311.add(p3111);
   p311.add(p3112);

   BankPanel p312=new BankPanel(5,5,5,5,new Color(60,110,60));
   p312.setBorder(BorderFactory.createLineBorder(Color.yellow,2));
   p312.setLayout(new GridLayout(100,1,5,5));

   BankPanel pdet[]=new BankPanel[100];

   tDet3=new JTextField[100];

   chk3=new JCheckBox[100];

    for(int i=0;i<100;i++)
    {
     pdet[i]=new BankPanel(1,1,1,1,new Color(60,110,60));
     pdet[i].setLayout(new BorderLayout(5,5));
 
      chk3[i]=new JCheckBox();
      chk3[i].setFont(new Font("lucida console",Font.PLAIN,14));      
      chk3[i].setVisible(false);
      chk3[i].addItemListener(this);

      tDet3[i]=new JTextField();
      tDet3[i].setFont(new Font("lucida console",Font.PLAIN,14));
      tDet3[i].setEditable(false);
      tDet3[i].setVisible(false);
      
      if(i%2==0)
      {
       chk3[i].setBackground(new Color(153,204,255));
       tDet3[i].setBackground(new Color(153,204,255));
      }
      else
      {
       chk3[i].setBackground(new Color(84,193,127));
       tDet3[i].setBackground(new Color(84,193,127));
      }
    
     pdet[i].add(chk3[i],BorderLayout.WEST);
     pdet[i].add(tDet3[i],BorderLayout.CENTER);

     p312.add(pdet[i]);
    } 

   JScrollPane jsp32=new JScrollPane(p312);
   jsp32.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
 

   BankPanel p313=new BankPanel(1,0,1,0,new Color(60,110,60));
   p313.setLayout(new BorderLayout(10,10));

    tMsg3=new JTextField();
    tMsg3.setFont(new Font("lucida console",Font.PLAIN,14));
    tMsg3.setText(" [i] Transaction(s) to delete");
    tMsg3.setEditable(false);
    tMsg3.setBackground(new Color(60,60,110)); 
    tMsg3.setForeground(Color.orange);


    bDel3=new JButton("Delete");
    bDel3.setFont(new Font("lucida console",Font.PLAIN,18));
    bDel3.setBackground(Color.red);
    bDel3.setForeground(Color.white);
    bDel3.setEnabled(false);

   p313.add(tMsg3,BorderLayout.CENTER);
   p313.add(bDel3,BorderLayout.EAST);

  p31.add(p311,BorderLayout.NORTH);
  p31.add(jsp32,BorderLayout.CENTER);
  p31.add(p313,BorderLayout.SOUTH);
   
  p3.setLayout(new BorderLayout(15,15));
  p3.add(jsp31,BorderLayout.WEST);
  p3.add(p31,BorderLayout.CENTER);

//--------------------------------END OF CANCEL PANEL-----------------------------//

  jtp.addTab("New Account",p1);
  jtp.setBackgroundAt(0,new Color(60,60,110));
  jtp.addTab("Transaction",p2);
  jtp.setBackgroundAt(1,new Color(120,80,150));
  jtp.addTab("Cancel Trans",p3);
  jtp.setBackgroundAt(2,new Color(60,110,60));

  jtp.addChangeListener(this);
  
  bSub1.addActionListener(this);
  bClr1.addActionListener(this);
  bCls1.addActionListener(this);
    
  bSub2.addActionListener(this);
  bClr2.addActionListener(this);
  bCls2.addActionListener(this);
  tAct2.addFocusListener(this);

  lst3.addListSelectionListener(this);
  bDel3.addActionListener(this);

 }
 
 public void valueChanged(ListSelectionEvent lse)
 {
  if(!lse.getValueIsAdjusting()&&lst3.getSelectedValue()!=null)
  {
   int id=Integer.parseInt(((String)lst3.getSelectedValue()).trim());
 
   try
   {
    getCust.setInt(1,id);
    ResultSet rs=getCust.executeQuery();
    rs.next();
    
    tNam3.setText(rs.getString("name"));
    bal=rs.getInt("balance");
    String sbal=bal+"";
   
    tMsg3.setText("["+selCnt+"] Transactions Selected for Deletion");

    for(int i=0;i<trnCount;i++)
    {
     tDet3[i].setVisible(false);
     chk3[i].setVisible(false);
     chk3[i].setEnabled(true);
     chk3[i].setSelected(false);
    }
    selCnt=0;
    tMsg3.setText("["+selCnt+"] Transactions Selected for Deletion");
    getTrans.setInt(1,id);
    rs=getTrans.executeQuery();

    SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/y");

    tVect=new Vector<TransDet>(); 
   
    for(trnCount=0;rs.next();trnCount++)
    {
     chk3[trnCount].setVisible(true);
     chk3[trnCount].setSelected(false);
     tDet3[trnCount].setVisible(true);
     
     String dt=sdf.format(rs.getDate("dot"));
     String  s=String.format("%d  %s  %7d  %s  %s",rs.getInt("trnid"),rs.getString("type"),
                               rs.getInt("amt"),dt,rs.getString("status"));
     tDet3[trnCount].setText(s);
     
     if(rs.getString("Status").equals("opening"))
     {
      chk3[trnCount].setEnabled(false);
     }
     bDel3.setEnabled(false);
     tVect.add(new TransDet(rs.getInt("trnid"),rs.getString("type"),rs.getInt("amt"),rs.getString("status")));
    }
    selCnt=0;
    tBal3.setText(sbal);
   }
   catch(SQLException e)
   {
    JOptionPane.showMessageDialog(this,"SQL Alert[List Selection] - "+e.getMessage()); 
   }
  }
 }

 public void stateChanged(ChangeEvent ce)
 {
  jtp.setBackgroundAt(0,new Color(60,60,110));
  jtp.setBackgroundAt(1,new Color(120,80,150));
  jtp.setBackgroundAt(2,new Color(60,110,60));
  
  if(jtp.getSelectedIndex()==0)
  {
   setSize(600,400);
   clearSelection();
   tNam2.setText("");
   tAmt2.setText("");
   tAct2.setText("");
  }
  else
  if(jtp.getSelectedIndex()==1)
  {
   setSize(600,500);
   tTrn2.setText(trnid+"");
   clearSelection();
  }
  else
  if(jtp.getSelectedIndex()==2)
  {
   setSize(600,500);
   tNam2.setText("");
   tAmt2.setText("");
   tAct2.setText("");
  }
 }

 void clearSelection()
 {
  for(int i=0;i<trnCount;i++)
  {
   chk3[i].setVisible(false);
   tDet3[i].setVisible(false);
  }
  lst3.clearSelection();
  tNam3.setText("");
  tBal3.setText("");
  tMsg3.setText("");
  bDel3.setEnabled(false);
 }

 public void focusLost(FocusEvent fe)
 {
  if(fe.getOppositeComponent()==tNam2 || fe.getOppositeComponent()==rCr || fe.getOppositeComponent()==rDr || fe.getOppositeComponent()==tAmt2 ||fe.getOppositeComponent()==bSub2)
  try
  {
   getCust.setString(1,tAct2.getText());
   ResultSet rs=getCust.executeQuery();
   if(rs.next())
   {
    tNam2.setText(rs.getString("name"));
    bal=rs.getInt("balance");
    //actid=Integer.parseInt(tAct2.getText());
   }
   else
   {
    JOptionPane.showMessageDialog(this,"Account Not Found - "+tAct2.getText());
    tAct2.select(0,tAct2.getText().length());
    tAct2.requestFocus();
   } 
  }
  catch(SQLException e)
  {
   JOptionPane.showMessageDialog(this,"SQL Alert[FOCUS LOST] - "+e.getMessage());
  } 
 }
 
 public void focusGained(FocusEvent fe)
 {
  tNam2.setText(""); 
  tAmt2.setText("");
 }

 public void itemStateChanged(ItemEvent ie)
 {
  int i;
  for(i=0;i<trnCount && ie.getSource()!=chk3[i];i++);
  TransDet td=tVect.get(i);

  if(td.status.equals("success"))
  {
   if(ie.getStateChange()==ItemEvent.SELECTED)
   {
    if(td.type.equals("CR"))
       bal=bal-td.amt;
    else
       bal=bal+td.amt;
   }
   else
   {
    if(td.type.equals("CR"))
       bal=bal+td.amt;
    else
       bal=bal-td.amt;
   }
   tBal3.setText(bal+"");
  }

  if(ie.getStateChange()==ItemEvent.SELECTED)
     selCnt++;
  else
     selCnt--;
  if(selCnt>0)
    bDel3.setEnabled(true);
  else
    bDel3.setEnabled(false);

  tMsg3.setText("["+selCnt+"] Transactions Selected for Deletion");
 }

 public void actionPerformed(ActionEvent ae)
 {
  if(ae.getSource()==bSub1)
  {
   try
   {
    if(tNam1.getText().length()==0)
    {
     JOptionPane.showMessageDialog(this,"Name cannot be blank");
     tNam1.requestFocus();
     return;
    }
    int bal=Integer.parseInt(tBal1.getText());
   
    insCust.setInt(1,actid);
    insCust.setString(2,tNam1.getText());
    insCust.setInt(3,bal);
    
    insCust.executeUpdate();
    insTrans.setInt(1,trnid);
    insTrans.setInt(2,actid);
    insTrans.setString(3,"CR");
    insTrans.setDate(4,new Date(new java.util.Date().getTime()));
    insTrans.setInt(5,bal);
    insTrans.setString(6,"opening");
    insTrans.executeUpdate();
    vct3.add(" "+actid+" ");

    lst3.setListData(vct3);
    
    actid++;
    trnid++;
    tAct1.setText(actid+"");
    actionPerformed(new ActionEvent(bClr1,0,"Clear"));  
   }
   catch(NumberFormatException e) 
   {
    JOptionPane.showMessageDialog(this,"Opening balance cannot be blank or non-digit");
    tBal1.select(0,tBal1.getText().length());
    tBal1.requestFocus();
   }
   catch(SQLException e)
   {
    JOptionPane.showMessageDialog(this,"SQL Alert[New Account] - "+e.getMessage());
   }
  }
  if(ae.getSource()==bClr1)
  {
   tNam1.setText("");
   tBal1.setText("");
   tNam1.requestFocus();
  }

  if(ae.getSource()==bClr2)
  {
   tNam2.setText("");
   tAmt2.setText("");
   tAct2.setText("");
   tAct2.requestFocus();
  }
  else   
  if(ae.getSource()==bCls1 || ae.getSource()==bCls2)
  {
   System.exit(0);
  } 
  else
  if(ae.getSource()==bSub2)
  {
   try
   {
    int amt=Integer.parseInt(tAmt2.getText());
    String type="DR";
    String status="success";
   
    if(rCr.isSelected())
    {
    type="CR";
    bal=bal+amt;
    } 
    else
    {
     if(amt>bal)
      status="failed";
     else
      bal=bal-amt;
    }

    Date dot=new Date(new java.util.Date().getTime());

    int id=Integer.parseInt(tAct2.getText());
   
    if(status=="success")
    {
     updCust.setInt(1,bal);
     updCust.setInt(2,id);
     updCust.executeUpdate();
     JOptionPane.showMessageDialog(this,"Transaction Successful");
    }
    else
    {
     JOptionPane.showMessageDialog(this,"Transaction Failed");
    }
       
    insTrans.setInt(1,trnid);
    insTrans.setInt(2,id);
    insTrans.setString(3,type);
    insTrans.setDate(4,dot);
    insTrans.setInt(5,amt);
    insTrans.setString(6,status);
    insTrans.executeUpdate();
    trnid++;
    tTrn2.setText(trnid+"");
    tAct2.setText("");
    tNam2.setText("");
    tAmt2.setText("");
   }
     
   catch(NumberFormatException e)
   {
    JOptionPane.showMessageDialog(this,"Amount cannot be blank and must be numeric");
    tAmt2.select(0,tAmt2.getText().length());
    tAmt2.requestFocus();
   }
   catch(SQLException e)
   {
    JOptionPane.showMessageDialog(this,"SQL Alert - "+e.getMessage());
   }  
  }
  else 
  if(ae.getSource()==bDel3)
  {
   for(int i=0;i<trnCount;i++)
   {
    if(chk3[i].isSelected())
    {
     try
     {
      delTrans.setInt(1,tVect.get(i).trnid);
      delTrans.executeUpdate();
      int id=Integer.parseInt(((String)lst3.getSelectedValue()).trim());
      updCust.setInt(1,bal);
      updCust.setInt(2,id);
      updCust.executeUpdate();
     }
     catch(SQLException e)
     {
      JOptionPane.showMessageDialog(this,"SQL Alert[Delete Trans] - "+e.getMessage());
     }
    }
   }
   int index=lst3.getSelectedIndex();
   valueChanged(new ListSelectionEvent(lst3,index,index,false)); 
  }
 } 
}

class BankPanel extends JPanel
{
 int top,left,bottom,right;
 Color bg=Color.black;

 BankPanel(int top,int left,int bottom,int right,Color bg)
 {
  this.top=top;
  this.left=left;
  this.bottom=bottom;
  this.right=right;
  this.bg=bg;
 }

 public void paintComponent(Graphics g)
 {
  super.paintComponent(g);
  setBackground(bg);
 }

 public Insets getInsets()						
 {
  return new Insets(top,left,bottom,right);
 }
}

class RoyalBank
{
 public static void main(String args[])
 {
  try
  {
   Class.forName("com.mysql.jdbc.Driver");
  }
  catch(ClassNotFoundException e)
  {
   JOptionPane.showMessageDialog(null,"driver not found - "+e.getMessage());
  }
  BankFrame bf=new BankFrame();
  bf.setVisible(true);
  bf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 }
}