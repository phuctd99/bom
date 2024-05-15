package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.character.enemy.ai.AIMedium;
import uet.oop.bomberman.graphics.Sprite;

public class Minvo extends Enemy {
    private Board _board;

    public Minvo(int x, int y, Board board) {
        super(x, y, board, Sprite.minvo_dead, Game.getBomberSpeed() * 1.5, 800);
        _board = board;
        _sprite = Sprite.minvo_right1;

        _ai = new AIMedium(_board.getBomber(), this);
        _direction = _ai.calculateDirection();
    }

    @Override
    protected void chooseSprite() {
        switch (_direction) {
            case 0:
            case 1:
                if (_moving)
                    _sprite = Sprite.movingSprite(Sprite.minvo_right1, Sprite.minvo_right2, Sprite.minvo_right3,
                            _animate, 60);
                else
                    _sprite = Sprite.minvo_left1;
                break;
            case 2:
            case 3:
                if (_moving)
                    _sprite = Sprite.movingSprite(Sprite.minvo_left1, Sprite.minvo_left2, Sprite.minvo_left3, _animate,
                            60);
                else
                    _sprite = Sprite.minvo_left1;
                break;
        }
    }

    // // Thêm hành vi cho Minvo

    // @Override
    // public void update() {
    // super.update();
    // Bomber bomber = _board.getBomber();
    // if (bomber != null) {
    // int bomberX = bomber.getXTile();
    // int bomberY = bomber.getYTile();
    // int minvoX = this.getXTile();
    // int minvoY = this.getYTile();

    // double distance = Math.sqrt(Math.pow(bomberX - minvoX, 2) + Math.pow(bomberY
    // - minvoY, 2));

    // // Kiểm tra xem khoảng cách giữa Minvo và Bomber có dưới một ngưỡng nhất định
    // // hay không
    // if (distance <= 3) {
    // moveTowardBomber();
    // placeBomb();
    // } else {
    // _direction = _ai.calculateDirection();
    // // move();
    // }
    // }
    // }

    // private void moveTowardBomber() {
    // Bomber bomber = _board.getBomber();
    // if (bomber != null) {
    // int bomberX = bomber.getXTile();
    // int bomberY = bomber.getYTile();
    // int minvoX = this.getXTile();
    // int minvoY = this.getYTile();

    // // hướng vector từ minvo đến bomber
    // int dx = bomberX - minvoX;
    // int dy = bomberY - minvoY;

    // // Chọn hướng đi gần nhất
    // if (Math.abs(dx) > Math.abs(dy)) {
    // if (dx > 0) {
    // _direction = 1; // Di chuyển sang phải
    // } else {
    // _direction = 3; // Di chuyển sang trái
    // }
    // } else {
    // if (dy > 0) {
    // _direction = 2; // Di chuyển xuống dưới
    // } else {
    // _direction = 0; // Di chuyển lên trên
    // }

    // }
    // }
    // // // di chuyển mivo
    // // move();

    // }

    // private void placeBomb() {
    // if (_board.detectNoEnemies()) {
    // return;
    // }
    // int bombRate = _board.getBombRate();
    // if (Math.random() < bombRate) { // Kiểm tra xác suất đặt bom
    // int xt = Coordinates.pixelToTile(_x + Game.TILES_SIZE / 2);
    // int yt = Coordinates.pixelToTile((_y + Game.TILES_SIZE / 2));
    // _board.addBomb(new Bomb(xt, yt, _board));
    // }
    // }

    // private void move() {
    // double xa = _x, ya = _y;

    // switch (_direction) {
    // case 0:
    // ya -= _speed;
    // break;
    // case 1:
    // xa += _speed;
    // break;
    // case 2:
    // ya += _speed;
    // break;
    // case 3:
    // xa -= _speed;
    // break;
    // }

    // // Kiểm tra va chạm với tường và cập nhật tọa độ đích đến nếu cần
    // if (!_board.detectWallCollision(xa, ya, _collisionRadius)) {
    // _x = xa;
    // _y = ya;
    // }
    // }
}
