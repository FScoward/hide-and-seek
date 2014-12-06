package frame

import java.awt.{Color, Font, Graphics}

import env.Environment

/**
 * Created by FScoward on 2014/11/30.
 */
class Tweet(str: String, var x: Int, y: Int) {
  val length = str.length

  def move() = {
    x -= 5
    x
  }

  def draw(g: Graphics) = {
    if (x > - str.length * Environment.fontSize) {
      val font = new Font("ＭＳ ゴシック", Font.PLAIN, Environment.fontSize)
      g.setFont(font)
      g.setColor(Color.white)
      g.drawString(str, x, y)
    }
  }

}
