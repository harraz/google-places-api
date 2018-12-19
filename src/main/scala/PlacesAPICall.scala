import java.io.{FileWriter, IOException}
import java.util.Locale

import com.google.gson.GsonBuilder
import com.google.maps.errors.ApiException
import com.google.maps.{FindPlaceFromTextRequest, GeoApiContext, PlacesApi}
import com.typesafe.config.ConfigFactory


object PlacesAPICall extends App {

  override def main(args: Array[String]): Unit = {

    // get the API key from conf file
    val api_key = ConfigFactory.load().getString("google.api-key.key")

    def context: GeoApiContext =
      new GeoApiContext.Builder()
          .apiKey(api_key)
        .build()

    //var searchString = "Suzanne Junger MUENCHNER STR. 187 85757 KARLSFELD B. MUENCHEN"

    try {

      // try to get search string from first argument
      var searchString: String = args{0}

      var locale: Locale = Locale.getDefault

      try {

        // make a call to PacesAPI and get the PLACE_ID
        val response = PlacesApi
          .findPlaceFromText(context, searchString, FindPlaceFromTextRequest.InputType.TEXT_QUERY)
          .fields(FindPlaceFromTextRequest.FieldMask.NAME, FindPlaceFromTextRequest.FieldMask.PLACE_ID)
          .locationBias(new FindPlaceFromTextRequest.LocationBiasIP)
          .await

        if (response.candidates.length == 0) {
          println(">WARN> No search results came back for: " + searchString)
          System.exit(0)
        } else {

          val placeDetails = PlacesApi.placeDetails(context, response.candidates(0).placeId, null).await

          //          System.out.println(response.candidates(0).placeId)
          //          def gson: Gson = new GsonBuilder().setPrettyPrinting().create()
          //          def gsonText=gson.toJson(placeDetails)
          //          System.out.println(gsonText)
          //          System.out.println(placeDetails.toString)

          try {
            val writer = new FileWriter("output.json")
              val gson = new GsonBuilder().create
              gson.toJson(placeDetails, writer)
              if (writer != null) writer.close()
            } catch  {
              case boo: IOException => println("ERROR> something went wrong when writing to JSON output file")
            }
        }

      } catch {

        case foo: ApiException => println(foo)
        case bar: InterruptedException => println(bar)
        case boo: IOException => println(boo)

      }

    } catch {
      case aiob: ArrayIndexOutOfBoundsException => println(">ERROR> missing input argument search term: " + aiob)
    }

    println(">INFO> done!")

  }

}
