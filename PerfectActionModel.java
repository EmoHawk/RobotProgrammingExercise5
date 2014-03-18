package localisation;

import lejos.nxt.Motor;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import rp.robotics.localisation.ActionModel;
import rp.robotics.localisation.GridPositionDistribution;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.Heading;

/**
 * Example structure for an action model that should move the probabilities 1
 * cell in the requested direction. In the case where the move would take the
 * robot into an obstacle or off the map, this model assumes the robot stayed in
 * one place. This is the same as the model presented in Robot Programming
 * Lecture 14.
 * 
 * Note that this class doesn't actually do this, instead it shows you a
 * <b>possible</b> structure for your action model.
 * 
 * @author nah
 * 
 */
public class PerfectActionModel implements ActionModel {

	DifferentialPilot pilot;
	PoseProvider pose;
	Navigator tomtom;
	
	public PerfectActionModel() {
		//pilot = new DifferentialPilot(wheelDiameter, trackWidth, Motor.A, Motor.B);
		//pose.setPose(new Pose());
		//tomtom = new Navigator(pilot, pose);
	}
	
	
	@Override
	public GridPositionDistribution updateAfterMove(
			GridPositionDistribution _from, Heading _heading) {

		// Create the new distribution that will result from applying the action
		// model
		GridPositionDistribution to = new GridPositionDistribution(_from);
		
		// Move the probability in the correct direction for the action
		if (_heading == Heading.PLUS_X) {
			movePlusX(_from, to);
		} else if (_heading == Heading.PLUS_Y) {
			// you could implement a movePlusY etc. or you could find a way do
			// do all moves in a single method. Hint: all changes are just + or
			// - 1 to an x or y value.
			movePlusY(_from, to);
		} else if (_heading == Heading.MINUS_X) {
			moveMinusX(_from, to);
		} else if (_heading == Heading.MINUS_Y) {
			moveMinusY(_from, to);
		}

		return to;
	}

	private void moveMinusY(GridPositionDistribution _from,
			GridPositionDistribution _to) {
		// TODO Auto-generated method stub
		for (int y = 1; y < _to.getGridHeight(); y++) {

			for (int x = 0; x < _to.getGridWidth(); x++) {

				
				
				// make sure to respect obstructed grid points
				if (!_to.isObstructed(x, y)) {
					// position before move
					int fromX = x;
					int fromY = y;
					float fromProb = _from.getProbability(fromX, fromY);
					
					// position after move
					int toX = x;
					int toY = y - 1;

					//tomtom.addWaypoint(toX, toY);
					//tomtom.followPath();
					// set probability for position after move
					if(!_to.isObstructed(toX, toY))
					{
						if(_to.isValidGridPoint(toX, toY))
						{
							_to.setProbability(toX, toY, fromProb);
						}
					}
					
					

				}
			}
		}
	}


	private void moveMinusX(GridPositionDistribution _from,
			GridPositionDistribution _to) {
		// TODO Auto-generated method stub
		for (int y = 0; y < _to.getGridHeight(); y++) {

			for (int x = 0; x < _to.getGridWidth(); x++) {

				
				
				// make sure to respect obstructed grid points
				if (!_to.isObstructed(x, y)) {
					// position before move
					int fromX = x;
					int fromY = y;
					float fromProb = _from.getProbability(fromX, fromY);
					
					// position after move
					int toX = x - 1;
					int toY = y;

					//tomtom.addWaypoint(toX, toY);
					//tomtom.followPath();
					// set probability for position after move
					_to.setProbability(toX, toY, fromProb);

				}
			}
		}
	}


	/**
	 * Move probabilities from _from one cell in the plus x direction into _to
	 * 
	 * @param _from
	 * @param _to
	 */
	private void movePlusX(GridPositionDistribution _from,
			GridPositionDistribution _to) {
		
		//System.out.println(_to.getGridHeight());
		//System.out.println(_to.getGridWidth());
		float singleProb = _from.getProbability(0, 0);
		GridMap gridmap = _to.getGridMap();
		// iterate through points updating as appropriate
		
		//System.out.println("height " + _to.getGridHeight());
		//System.out.println("witdh " + _to.getGridWidth());
		
		for (int y = 0; y < _to.getGridHeight() ; y++) {
			
			for (int x = _to.getGridWidth() - 1; x > 0; x--) {
								
				// make sure to respect obstructed grid points
				if (!_to.isObstructed(x, y)) {
					
					// the action model should work out all of the different
					// ways (x,y) in the _to grid could've been reached based on
					// the _from grid and the move taken (in this case
					// HEADING.PLUS_X)

					// for example if the only way to have got to _to (x,y) was
					// from _from (x-1, y) (i.e. there was a PLUS_X move from
					// (x-1, y) then you write that to the (x, y) value

					// The below code does not move the value, just copies
					// it to the same position
					
					// position before move					
					int fromX = x;
					int fromY = y;
					float fromProb = _from.getProbability(fromX, fromY);
					
					// position after move
					int toX = x + 1;
					int toY = y ;
					
					float toProb = 0;
									
					if(_to.isValidGridPoint(toX, toY)){
						toProb = _from.getProbability(toX, toY);
//						if(!_to.isObstructed(toX, toY))
//						{
							if(gridmap.isValidTransition(fromX, fromY, toX, toY))
							{
								_to.setProbability(toX, toY, fromProb + toProb);
								_to.setProbability(fromX, fromY, 0);

							}
							else
							{
								_to.setProbability(fromX, fromY, fromProb);
							}
//						} else {
//							_to.setProbability(fromX, fromY, fromProb + toProb);
//						}
						
						
					} 
					_to.normalise();
					
					
					//tomtom.addWaypoint(toX, toY);
					//tomtom.followPath();
					// set probability for position after move

				}
			}
		}
	}
	
	private void movePlusY(GridPositionDistribution _from,
			GridPositionDistribution _to) {

		// iterate through points updating as appropriate
		for (int y = 0; y < _to.getGridHeight(); y++) {

			for (int x = 0; x < _to.getGridWidth(); x++) {

				
				
				// make sure to respect obstructed grid points
				if (!_to.isObstructed(x, y)) {

					// position before move
					int fromX = x;
					int fromY = y;
					float fromProb = _from.getProbability(fromX, fromY);
					
					// position after move
					int toX = x;
					int toY = y + 1;
					
					//tomtom.addWaypoint(toX, toY);
					//tomtom.followPath();

					// set probability for position after move
					_to.setProbability(toX, toY, fromProb);

				}
			}
		}
	}
}
