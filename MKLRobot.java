package localisation;

import lejos.util.Delay;
import rp.robotics.localisation.ActionModel;
import rp.robotics.localisation.DummySensorModel;
import rp.robotics.localisation.GridPositionDistribution;
import rp.robotics.mapping.GridMap;
import rp.robotics.mapping.Heading;
import rp.robotics.mapping.LocalisationUtils;

public class MKLRobot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		GridMap gridMap = LocalisationUtils.createTrainingMap();

		// The probability distribution over the robot's location
		GridPositionDistribution distribution = new GridPositionDistribution(
				gridMap);

		// view the map with 2 pixels as 1 cm

		// ActionModel actionModel = new DummyActionModel();
		ActionModel actionModel = new PerfectActionModel();
		TrackedRobot rob = new TrackedRobot();

		DummySensorModel sensorModel = new DummySensorModel();
		
		Heading action = rob.getHeading();
		boolean canMoveAfter = rob.moveUntilJunction();
		GridPositionDistribution Distribution = actionModel.updateAfterMove(distribution, action);

		while (true) {
			// Do some action
			// E.g. attempting to move one node in the PLUS_X direction
			
			if(!canMoveAfter)
			{
				action = Heading.PLUS_Y;
			}
			// I'm faking movement by waiting for some time
			canMoveAfter = rob.moveJunction(action);
			
			

			// Once action is completed, apply action model based on the move
			// the robot took. This creates a new instance of
			// GridPoseDistribution and assigns it to distribution
			GridPositionDistribution newDistribution = actionModel.updateAfterMove(distribution, action);
			distribution = newDistribution;
			// Update visualisation. Only necessary because it needs to know
			// about the new distribution instance

			// Do some sensing
			// ...
			
			
			
			// I'm faking sensing by waiting for some time
			//Delay.msDelay(1000);

			// Once completed apply sensor model as appropriate. This changes
			// the distribution directly (i.e. by reference)
			//sensorModel.updateDistributionAfterSensing(distribution
			/**
			 * , include
			 * sensor readings
			 **/
			//);

			// Note, as the sensor model changes the distribution directly, the
			// visualisation will update automatically so
			// mapVis.setDistribution is not necessary after the sensor model

		}

	}
}
