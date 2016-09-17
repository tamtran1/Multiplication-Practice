/*
	Author: Tam Tran
	Description: this application is created to help my daughter exercise her multiplication
	table problems. It is intended to improve response reflex to standard multiplication
	problem. This application assumes user is already well versed in basic multiplication tables
	from 0 to 12.
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class MultPrac extends JFrame {
	
	/*
		this Card class use to create unique instances of every multiplication problems
		and inserted into list. necessary so no problem gets repeated. a and b are operand
		variables, c is result calculated from a and b, reponse is input from the user,
		index was used for debuging, feedback describes response compared to c, typically
		a boolean like "correct" or "wrong" value is recorded as string.
	*/
	private class Card {
		public int a, b, c, response, index;
		public String feedback;
	}
	
	/* instance variables */
	String problem, ans = "";
	int a, b, c, cardCntr = 0;
	double correctAns, numOfProblem = 30;
	long startTime, endTime;
	JFrame frame;
	JPanel leftPanel, centerPanel, rightPanel, resultPanel;
	JLabel operand, answer, feedback, resultContent;
	JScrollPane resultScrollPane;
	LinkedList<Card> deck;	
	Iterator<Card> deckIterator;
	Card tmpCard;
	
	/* the constructor*/
	public MultPrac() {
		System.out.println("multTest started...");
		generateDeck();
		initUI();
		run();
	}
	
	private void generateDeck() {
		deck = new LinkedList<Card>(); //initialize the linkedList
		
		for (int i = 0; i < 13; ++i)
			for (int j = 0; j < 13; ++j) {
				Card card = new Card();
				card.a = i;
				card.b = j;
				card.c = card.a * card.b;
				card.index = cardCntr;
				deck.add(card);
				++cardCntr;
			}
			
		cardCntr = 0; //reset the card counter for other use
		Collections.shuffle(deck); //shuffle the deck
		
		/* //this block is for debug only
		System.out.println ("cards in deck " + deck.size());
		deckIterator = deck.listIterator(0);
		while(deckIterator.hasNext()) {
			Card tmp = deckIterator.next();
			System.out.println ("a: " + tmp.a + " b: " + tmp.b + " c: " + tmp.c + " index: " + tmp.index + " currIndex: " + deck.indexOf(tmp));
		}
		*/
	}
	
	/*initialize all frames and panels for user interface*/
	private void initUI() {
		frame = new JFrame("multTest");
		
		leftPanel = new JPanel();
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setPreferredSize(new Dimension (250, 80));
		
		centerPanel = new JPanel();
		centerPanel.setBackground(Color.WHITE);
		centerPanel.setPreferredSize(new Dimension (130, 80));
		
		rightPanel = new JPanel();
		rightPanel.setBackground(Color.WHITE);
		rightPanel.setPreferredSize(new Dimension (240, 80));
		
		resultPanel = new JPanel();
		resultPanel.setBackground(Color.WHITE);
		resultPanel.setPreferredSize(new Dimension (500, 1100)); //adjust ver size to accomodate more results
		
		operand = new JLabel("", JLabel.CENTER);
		operand.setFont(new Font("Arial", Font.BOLD, 60));
		operand.setForeground(Color.BLACK);
		
		answer = new JLabel("", JLabel.CENTER);
		answer.setFont(new Font("Arial", Font.BOLD, 60));
		answer.setForeground(Color.BLACK);
		
		feedback = new JLabel("", JLabel.CENTER);
		feedback.setFont(new Font("Arial", Font.BOLD, 60));
		feedback.setForeground(Color.BLACK);
		
		resultContent = new JLabel("", JLabel.CENTER);
		resultContent.setFont(new Font("Arial", Font.PLAIN, 24));
		resultContent.setForeground(Color.BLACK);
		
		resultPanel.add(resultContent);
		
		resultScrollPane = new JScrollPane(resultPanel, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		resultScrollPane.setViewportView(resultPanel); //must specify this for scroll pane to work
		resultScrollPane.setPreferredSize(new Dimension (550, 500));
		
		leftPanel.add(operand);
		centerPanel.add(answer);
		rightPanel.add(feedback);
		
		frame.add(leftPanel, BorderLayout.WEST);
		frame.add(centerPanel, BorderLayout.CENTER);
		frame.add(rightPanel, BorderLayout.EAST);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	/*listen for key input and update as necessary*/
	private void run() {
		tmpCard = deck.get(cardCntr);
		operand.setText(tmpCard.a + " x " + tmpCard.b + " =");
		startTime = System.currentTimeMillis(); //start the timer
		
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((int)e.getKeyChar() > 47 && (int)e.getKeyChar() < 58 && ans.length() < 3)
					ans += e.getKeyChar(); //adding char to string
				
				if ((int)e.getKeyChar() == 8 && ans.length() > 0)
					ans = ans.substring (0, ans.length() - 1); //removing a char at end of string
				
				if ((int)e.getKeyChar() == 27)
					System.exit(0); //quit when user presses escape
				
				if ((int)e.getKeyChar() == 10) {
					if (ans != "" && Integer.parseInt(ans) == tmpCard.c) {
						feedback.setText("Correct");
						feedback.setForeground(Color.GREEN);
					} else {
						feedback.setText("Wrong!");
						feedback.setForeground(Color.RED);
					}
					
					tmpCard.response = (ans != "") ? Integer.parseInt(ans) : 0;
					tmpCard.feedback = (ans != "" && Integer.parseInt(ans) == tmpCard.c) ? "Correct" : "Wrong";
					if (deck.indexOf(tmpCard) == numOfProblem - 1) //stop after some number of problems
						result();
					++cardCntr;
					tmpCard = deck.get(cardCntr);
					operand.setText(tmpCard.a + " x " + tmpCard.b + " =");
					ans = "";
				}
				
				answer.setText(ans);
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
		});
	}
	
	/* calcutale total activity time and return time format as string */
	private String calcTime(long millis) {
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;
		String totalTime = String.format("%02d:%02d:%02d", hour, minute, second);
		return totalTime;
	}
	
	/* output the result to a file in the same directory */
	private void result() {
		endTime = System.currentTimeMillis();
		String totalTime = calcTime(endTime - startTime);
		String resultTxt = "<html>";
		correctAns = numOfProblem; //assuming all ans is correct, then deduct from number of Wrong 
		
		for (int i = 0; i < numOfProblem; ++i) {
			resultTxt += deck.get(i).a + " x " + deck.get(i).b + " = " +
						 deck.get(i).response +
						 " : " + deck.get(i).feedback;
			if (deck.get(i).feedback == "Wrong") {
				resultTxt += " : Correct Answer is " + deck.get(i).c + "<br>";
				--correctAns;
			} else
				resultTxt += "<br>";
		}
		
		resultTxt += "--------------------------------------<br>";
		resultTxt += "Total problems " + numOfProblem + "<br>";
		resultTxt += "Total correct " + correctAns + "<br>";
		resultTxt += "Total time " + totalTime + "<br>";
		resultTxt += "Your score " + String.format("%.0f%%", (100 * correctAns)/numOfProblem) + "<br></html>";
		
		resultContent.setText(resultTxt);
		
		frame.remove(leftPanel);
		frame.remove(centerPanel);
		frame.remove(rightPanel);
		frame.add(resultScrollPane);
		frame.pack();
		frame.setLocationRelativeTo(null);		
	}
}