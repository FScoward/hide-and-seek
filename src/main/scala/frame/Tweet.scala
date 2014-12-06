package frame

import java.awt.{Font, Graphics}

import env.Environment

/**
 * Created by FScoward on 2014/11/30.
 */
class Tweet(str: String, var x: Int, y: Int) {
  val length = str.length

  def move() = {
    x -= 2
    x
  }

  def draw(g: Graphics) = {
    if (x > - str.length * Environment.fontSize) {
      val font = new Font("ＭＳ ゴシック", Font.PLAIN, Environment.fontSize)
      g.setFont(font)
      g.drawString(str, x, y)
    }
  }

}
