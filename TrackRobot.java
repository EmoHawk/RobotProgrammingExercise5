package localisation;

import rp.robotics.mapping.Heading;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.addon.OpticalDistanceSensor;

public class TrackedRobot {
	/*Turn values:
	 * 110 = 90 degrees (don't ask)
	 * 220 = 180 degrees (our calibration sucks)
	 * */
	private static DifferentialPilot m_pilot;
	private Heading currentHeading;
	static boolean m_run = true;
	static OpticalDistanceSensor m_opticalDistanceSensor;
	private static int lightThreshold = 43;
	private static boolean notBusy = true;
	private static double max_speed;
	static LightSensor m_lightSensorR;
	static LightSensor m_lightSensorL;
	
	public TrackedRobot()
	{
		m_lightSensorR = new LightSensor(SensorPort.S2, true);
		m_lightSensorL = new LightSensor(SensorPort.S1, true);
		
		m_pilot = new DifferentialPilot(3, 12.75, Motor.C, Motor.B, false);
		m_opticalDistanceSensor = new OpticalDistanceSensor(SensorPort.S4);
		max_speed = m_pilot.getMaxTravelSpeed();
		currentHeading = Heading.PLUS_X;
		
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			@Override
			public void buttonReleased(Button _b) {
				m_run = false;
			}

			@Override
			public void buttonPressed(Button _b) {

			}
		});
		Button.waitForAnyPress();
	}
	
	public void turnToHeading(Heading _to)
	{
		switch(currentHeading)
		{
		case PLUS_X: 
			switch(_to)
			{
			case PLUS_X: ;
			break;
			case PLUS_Y: m_pilot.rotate(90);
			currentHeading = _to;
			break;
			case MINUS_X: m_pilot.rotate(180);
			currentHeading = _to;
			break;
			case MINUS_Y: m_pilot.rotate(-90);
			currentHeading = _to;
			break;
			};
		break;
		case PLUS_Y: 
			switch(_to)
			{
			case PLUS_X: m_pilot.rotate(-90);
			currentHeading = _to;
			break;
			case PLUS_Y: ;
			break;
			case MINUS_X: m_pilot.rotate(90);
			currentHeading = _to;
			break;
			case MINUS_Y: m_pilot.rotate(180);
			currentHeading = _to;
			break;
			};
		case MINUS_X: 
			switch(_to)
			{
			case PLUS_X: m_pilot.rotate(90);
			currentHeading = _to;
			break;
			case PLUS_Y: m_pilot.rotate(-90);
			currentHeading = _to;
			break;
			case MINUS_X: ;
			break;
			case MINUS_Y: m_pilot.rotate(180);
			currentHeading = _to;
			break;
			};
		break;
		case MINUS_Y: 
			switch(_to)
			{
			case PLUS_X: m_pilot.rotate(90);
			currentHeading = _to;
			break;
			case PLUS_Y: m_pilot.rotate(180);
			currentHeading = _to;
			break;
			case MINUS_X: m_pilot.rotate(-90);
			currentHeading = _to;
			break;
			case MINUS_Y: ;
			break;
			};
		break;
		}
	}
	
	/*public static void main(String[] args) 
	{
		TrackedRobot robot = new TrackedRobot();
		m_lightSensorR = new LightSensor(SensorPort.S2, true);
		m_lightSensorL = new LightSensor(SensorPort.S1, true);
		m_pilot.setTravelSpeed(max_speed / 3);
		while(m_run)
		{
			
//			m_pilot.rotate(117);
//			Delay.msDelay(1000);
//			
			m_pilot.forward();
			if(notBusy){
				makeDecision(m_lightSensorL.getLightValue(), m_lightSensorR.getLightValue());
			}
//			to very front of robot is 70
//			to close wall, no junction 70 - 75
//			to far wall, no junction ~150
		}
	}*/
	
	private static void makeDecision(int lightValueL, int lightValueR) 
	{
		// TODO Auto-generated method stub
		int distance = m_opticalDistanceSensor.getDistance();
		if(notBusy)
		{
			if(lightValueL < lightThreshold && lightValueR < lightThreshold)
			{
				//pilot.steer(20000);
				m_run = false;
				
				//at junction
				//atJunction(distance);
				
//				if(newDist < 300)
//				{
//					turnAround();
//					atJunction(newDist);
//				}
				//Delay.msDelay(1000);
			}
			else if(lightValueL < lightThreshold)
			{
				//turn right
		
				//Double turnRate = calcTurnRate(lightValueL);
				m_pilot.steer(8000);
				Thread.yield();
			}
			else if(lightValueR < lightThreshold)
			{
				// turn right
		
				//Double turnRate = calcTurnRate(lightValueR);
				m_pilot.steer(-8000);
				Thread.yield();
			}

		}

	}
	
	/*private static void atJunction(int dist)
	{
		
		//to very front of robot is 70
		//to close wall, no junction 70 - 75
		//to far wall, no junction ~150
		
		if(dist > 300)
		while
		{
			moveJunction();
		}
		else
		{
			goLeft();
		}
		
		Delay.msDelay(1000);
		
	}*/
	
	public Heading getHeading()
	{
		return currentHeading;
	}

	public boolean moveUntilJunction()
	{
		while(m_run)
		{
			m_pilot.forward();
			Thread.yield();
			if(notBusy){
				makeDecision(m_lightSensorL.getLightValue(), m_lightSensorR.getLightValue());
			}
		}
		m_pilot.stop();
		return m_opticalDistanceSensor.getDistance() < 300;
	}
	
	
	public boolean moveJunction(Heading h)
	{
		m_run = true;
		turnToHeading(h);
		while(m_run)
		{
			m_pilot.forward();
			Thread.yield();
			if(notBusy){
				makeDecision(m_lightSensorL.getLightValue(), m_lightSensorR.getLightValue());
			}
		}
		m_pilot.stop();
		return m_opticalDistanceSensor.getDistance() < 300;
		
	}
	
	private static void turnAround() {
		// TODO Auto-generated method stub
		notBusy = false;
		m_pilot.stop();
		m_pilot.rotate(234);
		Delay.msDelay(1000);
		m_pilot.stop();
		notBusy = true;
	}

	private static void goRight() {
		// TODO Auto-generated method stub
		notBusy = false;
		m_pilot.forward();
		Delay.msDelay(1500);
		m_pilot.stop();
		m_pilot.rotate(-117);
		//Delay.msDelay(500);
		m_pilot.stop();
		notBusy = true;
	}

	private static void goLeft() {
		// TODO Auto-generated method stub
		notBusy = false;
		m_pilot.forward();
		Delay.msDelay(1500);
		m_pilot.stop();
		m_pilot.rotate(117);
		//Delay.msDelay(500);
		m_pilot.stop();
		notBusy = true;
	}

	public static void goForward()
	{
		//m_pilot.forward();
		m_pilot.travel(1);
		//Delay.msDelay(300);
		//Thread.yield();
		//m_pilot.stop();
	}
	
}
