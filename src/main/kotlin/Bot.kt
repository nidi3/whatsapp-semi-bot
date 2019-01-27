import codeanticode.eliza.Eliza
import org.jsoup.Jsoup
import java.awt.*
import java.awt.datatransfer.*
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import java.net.URL
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFrame

val rob = Robot()
val rnd = Random()

fun <T> List<T>.choose() = this[rnd.nextInt(this.size)]

fun main() {
    val eliza = Eliza()
    val answers = mapOf(
            listOf("katze", "gato", "cat", "kitten", "gatto") to { -> ImageSelection(googleImages("kitten").choose()) },
            listOf("kotze", "vomit") to { -> ImageSelection(googleImages("kotze").choose()) },
            listOf("pizza") to { -> ImageSelection(googleImages("pizza").choose()) },
            listOf("einhorn") to { -> ImageSelection(googleImages("einhorn").choose()) },
            listOf("hilfe", "help") to { -> StringSelection("Hilf dir selbst!") },
            listOf("rosa", "rosarot") to { -> StringSelection("Bä!") })
    val tk = Toolkit.getDefaultToolkit()
    val screen = tk.screenSize
    val baseX = 300
    val baseY = screen.height - 200
    with(JFrame("put whatsapp here")) {

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        focusableWindowState = false
        isAlwaysOnTop = true
        location = Point(baseX, baseY - 200)
        size = Dimension(700, 200)
        isVisible = true
    }
    while (true) {
        Thread.sleep(5000)
        with(rob) {
            autoDelay = 50
            val x = findText(baseX, baseY + 20)
            if (x == null) println("not found")
            else {
                println("found $x")
                mouseMove(x, baseY + 20)
                mousePress(InputEvent.BUTTON1_DOWN_MASK)
                mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                Thread.sleep(50)
                mousePress(InputEvent.BUTTON1_DOWN_MASK)
                mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                Thread.sleep(50)
                mousePress(InputEvent.BUTTON1_DOWN_MASK)
                mouseRelease(InputEvent.BUTTON1_DOWN_MASK)

                keyPress(KeyEvent.VK_META)
                keyPress(KeyEvent.VK_C)
                keyRelease(KeyEvent.VK_C)
                keyRelease(KeyEvent.VK_META)

                keyPress(KeyEvent.VK_ESCAPE)
                keyRelease(KeyEvent.VK_ESCAPE)

                try {
                    val text = tk.systemClipboard.getData(DataFlavor.stringFlavor) as String
//                    val iter = answers.filter { (k, _) -> text.toLowerCase() in k }.iterator()
//                    val answer = if (iter.hasNext()) iter.next().value()
//                    else {
//                        if (!text.startsWith("_")) null
//                        else {
//                            ImageSelection(googleImages(text.substring(1)).choose())
//                        }
//                    }
                    val answer = if (text.startsWith("-")) null else StringSelection("- " + eliza.processInput(text))
                    if (answer != null) {
//                        val ix = findInput(baseX, baseY + 90)
                        val ix = baseX + 200
                        if (ix != null) {
                            mouseMove(ix, baseY + 90)
                            mousePress(InputEvent.BUTTON1_DOWN_MASK)
                            mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
                            tk.systemClipboard.setContents(answer, null)
                            keyPress(KeyEvent.VK_META)
                            keyPress(KeyEvent.VK_V)
                            keyRelease(KeyEvent.VK_V)
                            keyRelease(KeyEvent.VK_META)
                            Thread.sleep(1000)
                            keyPress(KeyEvent.VK_ENTER)
                            keyRelease(KeyEvent.VK_ENTER)
                        }
                    }
                } catch (e: UnsupportedFlavorException) {
                    println("wrong clipboard content")
                }
            }
        }
    }
}

fun isWhite(c: Color) = c.red == c.green && c.red == c.blue && c.red >= 215
fun isGreen(c: Color) = c.red in 190..220 && c.green in 230..250 && c.blue in 170..200

fun findText(x0: Int, y: Int): Int? {
    for (x in x0..x0 + 1000 step 5) {
//        rob.mouseMove(x, y)
        val col = rob.getPixelColor(x, y)
//        println(col)
        if (isWhite(col) || isGreen(col)) return x + 10
    }
    return null
}

fun findInput(x0: Int, y: Int): Int? {
    for (x in x0..x0 + 100 step 5) {
//        rob.mouseMove(x, y)
        val col = rob.getPixelColor(x, y)
//        println(col)
        if (isWhite(col)) return x + 30
    }
    return null
}

fun googleImages(query: String): List<BufferedImage> {
    val searchURL = "https://www.google.com/search?tbm=isch&q=$query"
    val doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get()
    val results = doc.select("img")
    return results.mapNotNull { res ->
        val src = res.attr("src")
        try {
            ImageIO.read(URL(src))
        } catch (e: Exception) {
            null
        }
    }
}

class ImageSelection(val image: Image) : Transferable {
    override fun getTransferDataFlavors(): Array<DataFlavor> {
        return arrayOf(DataFlavor.imageFlavor)
    }

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
        return DataFlavor.imageFlavor.equals(flavor)
    }

    override fun getTransferData(flavor: DataFlavor): Any {
        if (!DataFlavor.imageFlavor.equals(flavor)) {
            throw UnsupportedFlavorException(flavor)
        }
        return image
    }
}