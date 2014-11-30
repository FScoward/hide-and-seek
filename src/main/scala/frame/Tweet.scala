package frame

import java.awt.{Font, Graphics}

/**
 * Created by FScoward on 2014/11/30.
 */
class Tweet(str: String, var x: Int, y: Int) {
  val initPos = 1920

  def move() = {
    x -= 3
    x
  }

  def draw(g: Graphics) = {
    if (x > - str.length * 24) {
      val font = new Font("Arial", Font.PLAIN, 24)
      g.setFont(font)
      g.drawString(str, x, y)
    }
  }

}
