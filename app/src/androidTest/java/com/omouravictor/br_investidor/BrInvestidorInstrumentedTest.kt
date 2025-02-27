package com.omouravictor.br_investidor

import android.content.Intent
import android.view.KeyEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.pressKey
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.omouravictor.br_investidor.presenter.MainActivity
import com.omouravictor.br_investidor.presenter.init.LoginActivity
import com.omouravictor.br_investidor.presenter.user.UserUiModel
import com.omouravictor.br_investidor.util.AppConstants.USER_UI_MODEL_INTENT_EXTRA
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrInvestidorInstrumentedTest {

    private val auth = FirebaseAuth.getInstance()
    private val userTest = UserUiModel("oCy53tVwFrT5EUlZQJu8Je5Bi3v2", "teste@gmail.com")
    private val userTestPassword = "123456"

    @Test
    fun testSuccessfulLogin() {
        val activityScenario = ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.etLoginEmail))
            .perform(typeText(userTest.email), closeSoftKeyboard())

        onView(withId(R.id.etLoginPassword))
            .perform(typeText(userTestPassword), closeSoftKeyboard())

        onView(withId(R.id.incBtnLogin))
            .perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.mainLayout))
            .check(matches(isDisplayed()))

        activityScenario.close()
    }

    @Test
    fun testAddAssetAsLoggedUser() {
        auth.signInWithEmailAndPassword(userTest.email, userTestPassword)

        val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java).apply {
            putExtra(USER_UI_MODEL_INTENT_EXTRA, userTest)
        }

        val aActivityScenario = ActivityScenario.launch<MainActivity>(intent)

        onView(withId(R.id.addAssetMenuItem))
            .perform(click())

        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(typeText("MSFT"), pressKey(KeyEvent.KEYCODE_ENTER), closeSoftKeyboard())

        onView(withId(R.id.recycler_view_asset_search))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        Thread.sleep(5000)

        aActivityScenario.close()
    }
}