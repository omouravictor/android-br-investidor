<p align="center">
  <img src="https://github.com/omouravictor/assets/blob/main/assets/br-investidor/icon.png" width=15%>
</p>

<h1 align="center">BR Investidor</h1>

### [Download it on Google Play here](https://play.google.com/store/apps/details?id=com.omouravictor.br_investidor)

## Preview

![preview](https://github.com/user-attachments/assets/b1de4dde-04d5-40d1-8d37-e0f8c6c7bb39)

## Architecture

- MVVM

## Techs used

- [Firebase](https://firebase.google.com)
- [Navigation](https://developer.android.com/jetpack/androidx/releases/navigation)
- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Flow](https://developer.android.com/kotlin/flow)
- [OkHttp](https://square.github.io/okhttp/)
- [Retrofit2](https://square.github.io/retrofit/)
- [Glide](https://github.com/bumptech/glide)
- [WebView](https://developer.android.com/reference/android/webkit/WebView)
- [Facebook Shimmer](https://github.com/facebookarchive/shimmer-android)
- [Guava](https://github.com/google/guava)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)

## About this project

In this application you can:

- Use all essential features for free - no initial cost required.
- Manage your investments in a simple and efficient way.
- Register your assets and transactions anytime, anywhere.
- Access everything in one place: portfolio, news, and currency converter.
- Stay updated with real-time financial data and the latest market news.
- Navigate easily with a modern and intuitive interface.

## Why?

This project is part of my personal portfolio, so, I'll be happy if you could provide me any feedback about the project, code, structure or anything that you can report that could make me a better developer!

## How to run?

- Open the project with Android Studio.
- Go to https://rapidapi.com/alphavantage/api/alpha-vantage, create an account and generate a free API key for Alpha Vantage API.
- Go to https://freecurrencyapi.com, create an account and generate a free API key for Free Currency API.
- Go to https://newsapi.org, create an account and generate a free API key for News API.
- Access project root folder (android-br-investidor) and create a file with name "api.properties".
- Add the line below on "api.properties" file including your API keys.

```
STOCKS_API_BASE_URL="https://alpha-vantage.p.rapidapi.com/"
STOCKS_API_KEY="YOUR-API-KEY"
CURRENCY_EXCHANGE_RATES_API_BASE_URL="https://api.freecurrencyapi.com/v1/"
CURRENCY_EXCHANGE_RATES_API_KEY="YOUR-API-KEY"
NEWS_API_BASE_URL="https://newsapi.org/"
NEWS_API_KEY="YOUR-API-KEY"
```

- That's all :)
