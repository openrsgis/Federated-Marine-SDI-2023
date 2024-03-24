package whu.edu.cn.geostreamcube.scala.util

import com.madgag.gif.fmsware.AnimatedGifEncoder
import javax.imageio.ImageIO
import java.awt._
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util

object ToGifUtil {
  def main(args: Array[String]): Unit = {
    val imageList = new util.ArrayList[BufferedImage]
    for (i <- 2000 until 2023) {
      for (j <- 1 until 13) {
        try {
          val input = new File("D:/IDEAProjects/WordCount/src/resultPNG/SPEI_" + i + String.format("%02d", j.asInstanceOf[AnyRef]) + "_scale03.png")
          val image: BufferedImage = ImageIO.read(input)
          imageList.add(image)
        } catch {
          case e: IOException =>
            e.printStackTrace()
        }
      }
    }
    try imagesToGif(imageList, "D:/IDEAProjects/WordCount/src/resultGIF/result.gif")
    catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }

  def imagesToGif(imageList: util.List[BufferedImage], outputPath: String): Unit = {
    val encoder = new AnimatedGifEncoder
    encoder.start(outputPath)
    encoder.setRepeat(0)
    // 设置每一帧之间的延迟时间
    encoder.setDelay(1000)
    for (i <- 0 until imageList.size) {
      val bufferedImage: BufferedImage = imageList.get(i)
      val height: Int = bufferedImage.getHeight
      val width: Int = bufferedImage.getWidth
      val zoomImage = new BufferedImage(width, height, 3)
      val image: Image = bufferedImage.getScaledInstance(width, height, Image.SCALE_SMOOTH)
      val gc: Graphics = zoomImage.getGraphics
      gc.setColor(Color.RED)
      gc.drawImage(image, 0, 0, null)
      // 在右下角绘制字符串
      val year: Int = 2000 + i / 12
      val month: Int = i - i / 12 * 12 + 1
      val text: String = year + " - " + String.format("%02d", month.asInstanceOf[AnyRef])
      val font = new Font("Arial", Font.BOLD, 20)
      gc.setFont(font)
      val metrics: FontMetrics = gc.getFontMetrics(font)
      val textWidth: Int = metrics.stringWidth(text)
      val textHeight: Int = metrics.getHeight
      gc.drawString(text, width - textWidth - 10, height - textHeight - 10)
      encoder.addFrame(zoomImage)
    }
    encoder.finish
    val outFile = new File(outputPath)
    val image: BufferedImage = ImageIO.read(outFile)
    ImageIO.write(image, outFile.getName, outFile)
  }
}

