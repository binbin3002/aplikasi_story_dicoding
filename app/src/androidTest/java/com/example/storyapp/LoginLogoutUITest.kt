package com.example.storyapp


import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.ui.story.ui.StoryActivity
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import com.example.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.hamcrest.CoreMatchers.not


@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LoginLogoutUITest {

    private val validEmailSample = "binbaybul@gmail.com"
    private val validPasswordSample = "binbay123"
    private val invalidFormatEmailSample = "wnferfnrj"
    private val invalidFormatPasswordSample = "43241"
    private val wrongEmailSample = "tadjnvjfn@yahoo.com"
    private val wrongPasswordSample = "12345678"

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
        Intents.init()
    }

    @Test
    fun test01_testFailedLoginWithEmptyCredentials() {
        onView(withId(R.id.login_button)).check(matches(not(isEnabled())))
        onView(withId(R.id.login_button)).perform(click())
    }

    @Test
    fun test02_testFailedLoginWithInvalidEmailFormat() {
        onView(withId(R.id.ed_login_email)).perform(typeText(invalidFormatEmailSample))
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(typeText(validPasswordSample))
        closeSoftKeyboard()

        onView(withId(R.id.login_button)).check(matches(not(isEnabled())))

        onView(withId(R.id.login_button)).perform(click())
    }

    @Test
    fun test03_testFailedLoginWithInvalidPasswordFormat() {
        onView(withId(R.id.ed_login_email)).perform(typeText(validEmailSample))
        closeSoftKeyboard()

        onView(withId(R.id.ed_login_password)).perform(typeText(invalidFormatPasswordSample))
        closeSoftKeyboard()

        onView(withId(R.id.login_button)).check(matches(not(isEnabled())))

        onView(withId(R.id.login_button)).perform(click())
    }

    @Test
    fun test04_testFailedLoginWithWrongCredentials() {
        onView(withId(R.id.ed_login_email)).perform(typeText(wrongEmailSample))
        closeSoftKeyboard()
        onView(withId(R.id.ed_login_password)).perform(typeText(wrongPasswordSample))
        closeSoftKeyboard()

        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))
    }

    @Test
    fun test05_testLoginLogout() {
        onView(withId(R.id.ed_login_email)).perform(typeText(validEmailSample))
        closeSoftKeyboard()
        onView(withId(R.id.ed_login_password)).perform(typeText(validPasswordSample))
        closeSoftKeyboard()
        onView(withId(R.id.login_button)).perform(click())

        Intents.intended(hasComponent(StoryActivity::class.java.name), Intents.times(2))

        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.logout)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).inRoot(isDialog()).perform(click())

        Intents.intended(hasComponent(MainActivity::class.java.name), Intents.times(1))
    }

    @Test
    fun test06_testLoginLogoutCancel(){
        onView(withId(R.id.ed_login_email)).perform(typeText(validEmailSample))
        closeSoftKeyboard()
        onView(withId(R.id.ed_login_password)).perform(typeText(validPasswordSample))
        closeSoftKeyboard()
        onView(withId(R.id.login_button)).perform(click())

        Intents.intended(hasComponent(StoryActivity::class.java.name), Intents.times(2))

        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.logout)).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(R.string.cancel)).inRoot(isDialog()).perform(click())

        onView(withId(R.id.story)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
        Intents.release()
    }
}