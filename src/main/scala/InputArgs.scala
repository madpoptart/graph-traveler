import java.io.File
import scopt.{OParser, OParserBuilder}

object InputArgs {

  case class Config(
                   input: File = new File("."),
                   all: Boolean = false

                   )

  val builder: OParserBuilder[Config] = OParser.builder[Config]

  val parser: OParser[Unit, Config] = {
  import builder._
  OParser.sequence(
    programName("graph-traveler"),
    head("traveler", "0.x"),
    opt[Unit]('a',"all")
      .action((_, c) => c.copy(all = true))
      .text("Run all the tests and print \"Expected output\""),
    opt[String]('i', "input")
      .required()
      .valueName("<file>")
      .action((x, c) => c.copy(input = new File(x)))
      .text("File containing a comma separated input graph"),
    help("help").text("prints this usage text"),
      checkConfig(
        c =>
          if (!c.input.exists()) failure("Cannot read file")
          else success)

  )
}
}
