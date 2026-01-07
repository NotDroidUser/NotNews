package com.notdroid.notnews.db.newsApi

/**
 * Data class to make {@link NewsApiResponse} or {@link NewsSourceResponse} into a non null
 * error for ease the work if it is one
 *
 * From the api
 *
 * Error codes
 *
 * When an HTTP error is returned we populate the code and message properties in the response containing more information. Here are the possible options:
 *
 *   apiKeyDisabled - Your API key has been disabled.
 *   apiKeyExhausted - Your API key has no more requests available.
 *   apiKeyInvalid - Your API key hasn't been entered correctly. Double check it and try again.
 *   apiKeyMissing - Your API key is missing from the request. Append it to the request with one of these methods.
 *   parameterInvalid - You've included a parameter in your request which is currently not supported. Check the message property for more details.
 *   parametersMissing - Required parameters are missing from the request and it cannot be completed. Check the message property for more details.
 *   rateLimited - You have been rate limited. Back off for a while before trying the request again.
 *   sourcesTooMany - You have requested too many sources in a single request. Try splitting the request into 2 smaller requests.
 *   sourceDoesNotExist - You have requested a source which does not exist.
 *   unexpectedError - This shouldn't happen, and if it does then it's our fault, not yours. Try the request again shortly.
 *
 * * */
data class NewsError(val status:String,
                     val code:String,
                     val message: String){

  companion object {
    const val DEFAULT_ERROR = "This \"Sounds\" unlikely a real error tbh"
    const val DEFAULT_CODE = "404"
  }

  constructor(everything: NewsApiResponse):this(everything.status,
    everything.code?: DEFAULT_CODE,
    everything.message?: DEFAULT_ERROR
  )

  constructor(sources: NewsSourceResponse):this(sources.status,
    sources.code?: DEFAULT_CODE,
    sources.message?: DEFAULT_ERROR
  )
}


