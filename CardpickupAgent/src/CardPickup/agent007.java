package CardPickup;

import java.util.Random;

/**
 * Some important variables inherited from the Player Class:
 * protected Node[] graph; //Contains the entire graph
 * protected Hand hand; //Contains your current hand (Use the cardsHole array list)
 * protected int turnsRemaining; //Number of turns before the game ends
 * protected int currentNode; //Your current location
 * protected int oppNode; //Opponent's current position
 * protected Card oppLastCard;	//Opponent's last picked up card
 * 
 * Important methods inherited from Player Class:
 * Method that is used to determine if a move is valid. This method should be used to help players
 * determine if their actions are valid. GameMaster has a local copy of this method and all the
 * required variables (such as the true graph), so manipulating the variables to turn a previously
 * invalid action in to a "valid" one will not help you as the GameMaster will still see the action 
 * as invalid.
 * protected boolean isValidAction(Action a); //This method can be used to determine if an action is valid
 *
 * NOTE TO STUDENTS: The game master will only tell the player the results of your and your opponents actions.
 * It will not update your graph for you. That is something we left you to do so that you can update your
 * graphs, opponent hand, horoscope, etc. intelligently and however you like.

 * 
 * @author Marcus Gutierrez
 * @version 04/15/2015
 */
public class agent007 extends Player{
	protected final String newName = "agent007"; //Overwrite this variable in your player subclass

	/**Do not alter this constructor as nothing has been initialized yet. Please use initialize() instead*/
	public agent007() {
		super();
		playerName = newName;
	}

	public void initialize() {

	}

	/**
	 * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of the opponent's moves. 
	 * GameMaster will call this to update your player on the opponent's actions. This method is called
	 * after the opponent has made a move.
	 * 
	 * @param opponentNode Opponent's current location
	 * @param opponentPickedUp Notifies if the opponent picked up a card last turn
	 * @param c The card that the opponent picked up, if any (null if the opponent did not pick up a card)
	 */
	protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
		oppNode = opponentNode;
		if(opponentPickedUp){
			graph[opponentNode].clearPossibleCards();
			oppLastCard = c;
		}
		else
			oppLastCard = null;
	}

	/**
	 * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of your results.
	 * GameMaster will call this to update you on your actions.
	 *
	 * @param currentNode Opponent's current location
	 * @param c The card that you picked up, if any (null if you did not pick up a card)
	 */
	protected void actionResult(int currentNode, Card c){
		this.currentNode = currentNode;
		if(c!=null){
			addCardToHand(c);
			graph[currentNode].clearPossibleCards();
		}
	}

	/**
	 * Player logic goes here
	 */
	public Action makeAction() {
		float maxNode = 0;
		int maxNeighbor = 0;
		
		HandEvaluator eval = new HandEvaluator();
		for(int neighbors = 0; neighbors < graph[currentNode].getNeighborAmount(); neighbors++){
			Hand temp = new Hand();

			for(int down = 0; down < hand.getNumHole(); down++){
				temp.addHoleCard(hand.getHoleCard(down));
			}

			for(int up = 0; up < hand.getNumUp(); up++){
				temp.addUpCard(hand.getHoleCard(up));
			}

			temp.addUpCard(graph[currentNode].getNeighbor(neighbors).getPossibleCards().get(evaluateNode(graph[currentNode].getNeighbor(neighbors))));
			if(maxNode < eval.rankHand(temp)){
				maxNode = evaluateNode(graph[currentNode].getNeighbor(neighbors));
				maxNeighbor = neighbors;
			}
		}
		
		if(graph[currentNode].getNeighbor(maxNeighbor).getPossibleCards().size() != 0){
			return new Action(ActionType.PICKUP, maxNeighbor);

		}
		
		return new Action(ActionType.MOVE, maxNeighbor);

	}




//	private int evaluateGraph(){
//
//		float [] nodeMaxValue = new float [graph.length]; 
//
//		//go through all the nodes to find out which one has the highest reward
//		for(int i = 0; i < graph.length; i++){
//			nodeMaxValue[i] = evaluateNode(graph[i]);
//		}
//
//
//		//find the biggest value 
//		float max = 0;
//		int index = -1;
//		for(int size = 0; size < nodeMaxValue.length; size++){
//			if(max < nodeMaxValue[size]){
//				max = nodeMaxValue[size];
//				index = size;
//			}
//		}
//
//		return index;
//	}


	public int evaluateNode(Node node){
		HandEvaluator evaluator = new HandEvaluator();

		//max hand value for each node
		float max = evaluator.rankHand(hand);
		int cardIndex = -1;

		//make a hand with each of the possible hands
		for(int j = 0; j < node.getPossibleCards().size(); j++){

			//create a temporary hand for each possible card in the node and evaluate each of them
			Hand temp = new Hand();

			for(int down = 0; down < hand.getNumHole(); down++){
				temp.addHoleCard(hand.getHoleCard(down));
			}

			for(int up = 0; up < hand.getNumUp(); up++){
				temp.addUpCard(hand.getHoleCard(up));
			}

			temp.addUpCard(node.getPossibleCards().get(j));

			if(max < Math.max(max, evaluator.rankHand(temp))){
				cardIndex = j;
				max = Math.max(max, evaluator.rankHand(temp));
			}
		}
		return cardIndex;
	}


}