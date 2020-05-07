package ServeurJeu.GUI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.*;
import javax.swing.JPanel;
import ServeurJeu.Maitre;
import java.awt.GridLayout;

public class ServerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final Maitre maitre;
	private JLabel stateLabel;

	   public ServerFrame(Maitre controler)
	   {
	      this.maitre = controler;
		  this.setSize(400,200);
	      this.setLocation(200,200);
	      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	      //this.setIconImage(serverIcon.jpeg);
	      stateLabel = new JLabel("  Server est demarre  ", SwingConstants.CENTER);
	      
	      //made the panels with components
	      this.construction();
	   }

	   private void construction()
	   {
		  JPanel buttons = new JPanel();
		  JPanel info = new JPanel();
		  
		  // fill buttons panel
		  //buttons.setBackground(Color.red);
		  buttons.setPreferredSize(new Dimension(100,200));
		  GridLayout buttonsGrid = new GridLayout(8,1);
		  buttons.setLayout(buttonsGrid);
		  
		  // add buttons
		  JLabel nullLabel = new JLabel("");
		  JButton onButton = new JButton(Maitre.SETON);
		  JButton offButton = new JButton(Maitre.SETOFF);
		  JButton resetButton = new JButton(Maitre.RESET);
		  JButton exitButton = new JButton(Maitre.EXIT);
		  
		  buttons.add(nullLabel);
		  buttons.add(onButton);
		  buttons.add(offButton);
		  buttons.add(resetButton);
		  buttons.add(exitButton);
		  
		  info.setPreferredSize(new Dimension(300,200));
		  //info.setBackground(Color.blue);
		  
		  // add and transform state label
		  //stateLabel.setBounds(10, 10, 300, 50);
		  stateLabel.setBackground(Color.GREEN);
		  
		  //stateLabel.setForeground(Color.GREEN);
		  stateLabel.setSize(300, 50);
		  stateLabel.setOpaque(true);
		  stateLabel.setBorder(BorderFactory.createBevelBorder(NORMAL));
		  
		  info.add(stateLabel);
		  info.validate();
		 		  
		  ServerButtonsListener buttonsListener = new ServerButtonsListener(this);
		  onButton.addActionListener(buttonsListener);
		  offButton.addActionListener(buttonsListener);
		  resetButton.addActionListener(buttonsListener);
		  exitButton.addActionListener(buttonsListener);
		  
		  this.getContentPane().add(buttons, BorderLayout.WEST);
		  this.getContentPane().add(info, BorderLayout.EAST);
		
	   }

	   // Makes the frame visible.
	   public void showIt(){
	     this.setVisible(true);
	   }

	   // Makes the frame visible and sets the title text.
	   public void showIt(String title){
	     this.setTitle(title);
	     this.setVisible(true);
	   }

	   // Makes the frame visible and sets the title text
	   // and the position of the window.

	   public void showIt(String title, int x, int y){
	     this.setTitle(title);
	     this.setLocation(x,y);
	     this.setVisible(true);
	   }

	   // Makes the frame invisible.
	   public void hideIt(){
	     this.setVisible(false);
	   }
	   
	   public void setLabelOn(){
		   //stateLabel.setText("  Server is On  ");
		   stateLabel.setText("  Server demarrer!  ");
		   stateLabel.setBackground(Color.GREEN);
	   }
	   
	   public void setLabelTerminating(){
		   ///stateLabel.setText("  Server is Terminating...  ");
		   stateLabel.setText("  Server va se fermer...  ");
		   stateLabel.setBackground(Color.YELLOW);
	   }
	
	   public void setLabelOff(){
		   //stateLabel.setText("  Server is Off  ");
		   stateLabel.setText("  Server fermer!  ");
		   stateLabel.setBackground(Color.RED);
	   }

	public void sendMessageToServer(String actionCommand) {
		// TODO Auto-generated method stub
		maitre.treatCommand(actionCommand);		
	}
	
}
