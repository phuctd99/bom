package uet.oop.bomberman.entities.character;

class Waypoint {
	double moveX;
	double moveY;
	double moveDuration;
	double moveDx;
	double moveDy;
	
	public boolean started = false;
	public double moveDestX;
	public double moveDestY;

	public Waypoint(double moveX, double moveY, double moveDuration) {
		this.moveX = moveX;
		this.moveY = moveY;
		this.moveDuration = moveDuration;
		this.moveDx = moveX / moveDuration;
		this.moveDy = moveY / moveDuration;
	}
}