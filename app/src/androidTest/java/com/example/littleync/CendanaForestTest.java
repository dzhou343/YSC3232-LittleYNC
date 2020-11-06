package com.example.littleync;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.littleync.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CendanaForestTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
"android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void cendanaForestTest() {
        ViewInteraction appCompatEditText = onView(
allOf(withId(R.id.input_email),
childAtPosition(
allOf(withId(R.id.login_main),
childAtPosition(
withId(android.R.id.content),
0)),
0),
isDisplayed()));
        appCompatEditText.perform(click());
        
        ViewInteraction appCompatEditText2 = onView(
allOf(withId(R.id.input_email),
childAtPosition(
allOf(withId(R.id.login_main),
childAtPosition(
withId(android.R.id.content),
0)),
0),
isDisplayed()));
        appCompatEditText2.perform(click());
        
        ViewInteraction appCompatEditText3 = onView(
allOf(withId(R.id.input_email),
childAtPosition(
allOf(withId(R.id.login_main),
childAtPosition(
withId(android.R.id.content),
0)),
0),
isDisplayed()));
        appCompatEditText3.perform(replaceText("d@g.com"), closeSoftKeyboard());
        
        ViewInteraction appCompatEditText4 = onView(
allOf(withId(R.id.input_password),
childAtPosition(
allOf(withId(R.id.login_main),
childAtPosition(
withId(android.R.id.content),
0)),
1),
isDisplayed()));
        appCompatEditText4.perform(replaceText("test123"), closeSoftKeyboard());
        
        ViewInteraction appCompatEditText5 = onView(
allOf(withId(R.id.input_password), withText("test123"),
childAtPosition(
allOf(withId(R.id.login_main),
childAtPosition(
withId(android.R.id.content),
0)),
1),
isDisplayed()));
        appCompatEditText5.perform(pressImeActionButton());
        
        ViewInteraction appCompatButton = onView(
allOf(withId(R.id.login_btn), withText("Login"),
childAtPosition(
allOf(withId(R.id.login_main),
childAtPosition(
withId(android.R.id.content),
0)),
2),
isDisplayed()));
        appCompatButton.perform(click());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatButton2 = onView(
allOf(withId(R.id.cendana_forest_travel), withText("Cendana Forest"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
3),
isDisplayed()));
        appCompatButton2.perform(click());
        
        ViewInteraction textView = onView(
allOf(withId(R.id.stamina_section), withText("Stamina: 50 / 50"),
withParent(allOf(withId(R.id.forest),
withParent(withId(android.R.id.content)))),
isDisplayed()));
        textView.check(matches(withText("Stamina: 50 / 50")));
        
        ViewInteraction textView2 = onView(
allOf(withId(R.id.time_left), withText("04:10"),
withParent(allOf(withId(R.id.time_section),
withParent(withId(R.id.forest)))),
isDisplayed()));
        textView2.check(matches(withText("04:10")));
        
        ViewInteraction appCompatImageButton = onView(
allOf(withId(R.id.start_pause_resume_button),
childAtPosition(
allOf(withId(R.id.time_section),
childAtPosition(
withId(R.id.forest),
5)),
2),
isDisplayed()));
        appCompatImageButton.perform(click());
        
        ViewInteraction appCompatImageButton2 = onView(
allOf(withId(R.id.start_pause_resume_button),
childAtPosition(
allOf(withId(R.id.time_section),
childAtPosition(
withId(R.id.forest),
5)),
2),
isDisplayed()));
        appCompatImageButton2.perform(click());
        
        ViewInteraction imageButton = onView(
allOf(withId(R.id.reset_button),
withParent(allOf(withId(R.id.time_section),
withParent(withId(R.id.forest)))),
isDisplayed()));
        imageButton.check(matches(isDisplayed()));
        
        ViewInteraction appCompatImageButton3 = onView(
allOf(withContentDescription("Navigate up"),
childAtPosition(
allOf(withId(R.id.action_bar),
childAtPosition(
withId(R.id.action_bar_container),
0)),
1),
isDisplayed()));
        appCompatImageButton3.perform(click());
        
        ViewInteraction appCompatButton3 = onView(
allOf(withId(R.id.logout), withText("Logout"),
childAtPosition(
childAtPosition(
withId(android.R.id.content),
0),
6),
isDisplayed()));
        appCompatButton3.perform(click());
        }
    
    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
