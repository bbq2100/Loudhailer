package loudhailer

import java.io.File
import java.nio.file.{Files, Paths}
import javax.sound.sampled.DataLine.Info
import javax.sound.sampled._

import cats.data.Xor

/* copied from https://rosettacode.org/wiki/Record_sound#Scala */
object SoundRecorder {

  val recording = "sample.wav"

  type Data = Array[Byte]

  type Sample = Xor[Error, Data]

  def sample: Sample = {
    // record duration, in milliseconds
    val RECORD_TIME = 3000 // 3 seconds

    // path and format of the wav file
    val (wavFile, fileType) = new File(recording) -> AudioFileFormat.Type.WAVE
    val format = new AudioFormat(/*sampleRate =*/ 16000f,
      /*sampleSizeInBits =*/ 16,
      /*channels =*/ 2,
      /*signed =*/ true,
      /*bigEndian =*/ true)

    val info: Info = new DataLine.Info(classOf[TargetDataLine], format)
    val line: TargetDataLine = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]

    // Entry to run the program

    // Creates a new thread that waits for a specified of time before stopping
    new Thread(new Runnable() {
      def run() {
        try {
          Thread.sleep(RECORD_TIME)
        } catch {
          case ex: InterruptedException => ex.printStackTrace()
        }
        finally {
          line.stop()
          line.close()
        }
        println("Finished")
      }
    }).start()

    //Captures the sound and record into a WAV file
    try {
      // checks if system supports the data line
      if (AudioSystem.isLineSupported(info)) {
        line.open(format)
        line.start() // start capturing
        println("Recording started")
        AudioSystem.write(new AudioInputStream(line), fileType, wavFile)
        Xor.Right(Files.readAllBytes(Paths.get(SoundRecorder.recording)))
      } else Xor.Left(new Error("Line not supported"))
    } catch {
      case _: Throwable => Xor.Left(new Error("Line not supported"))
    }
  }
}