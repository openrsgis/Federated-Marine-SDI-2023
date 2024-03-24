package whu.edu.cn.geostreamcube.scala.entity

import geotrellis.raster.io.geotiff.SinglebandGeoTiff
import geotrellis.raster.io.geotiff.reader.GeoTiffReader
import org.apache.hadoop.fs.Path
import org.apache.hadoop.mapreduce.{InputSplit, JobContext, RecordReader, TaskAttemptContext}
import org.apache.hadoop.mapreduce.lib.input.{FileInputFormat, FileSplit}

class GeoTIFFInputFormat extends FileInputFormat[Long, SinglebandGeoTiff] {
  override def createRecordReader(
                                   split: InputSplit,
                                   context: TaskAttemptContext
                                 ): RecordReader[Long, SinglebandGeoTiff] = new GeoTIFFRecordReader

  override def isSplitable(context: JobContext, filename: Path): Boolean = false
}

class GeoTIFFRecordReader extends RecordReader[Long, SinglebandGeoTiff] {
  private var filePath: Path = _
  private var isRead: Boolean = false

  override def initialize(split: InputSplit, context: TaskAttemptContext): Unit = {
    val fileSplit: FileSplit = split.asInstanceOf[FileSplit]
    filePath = fileSplit.getPath
  }

  override def nextKeyValue(): Boolean = !isRead

  override def getCurrentKey: Long = filePath.toString.hashCode.toLong

  override def getCurrentValue: SinglebandGeoTiff = {
    // 在这里实现读取和解析 TIFF 文件的逻辑
    if (!isRead) {
      val singlebandGeoTiff: SinglebandGeoTiff = GeoTiffReader.readSingleband(filePath.toString)
      isRead = true
      singlebandGeoTiff
    }
    else {
      null
    }
  }

  override def getProgress: Float = if (isRead) 1.0f else 0.0f

  override def close(): Unit = {}
}
