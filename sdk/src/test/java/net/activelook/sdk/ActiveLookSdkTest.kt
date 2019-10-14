package net.activelook.sdk

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.activelook.sdk.operation.ActiveLookOperation
import net.activelook.sdk.operation.ActiveLookOperationProcessor
import net.activelook.sdk.screen.Screen
import net.activelook.sdk.session.GattSession
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ActiveLookSdkTest {

    lateinit var appContext: Context

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun testInstanceNotNull() {
        val instance = ActiveLookSdk.getInstance(appContext)
        assertNotNull(instance)
    }

    @Test
    fun testLoadingModeLazy() {
        val instance = ActiveLookSdk.newInstance(appContext)
        val processor = mockk<ActiveLookOperationProcessor>()
        val session = mockk<GattSession>()

        val screen = Screen.Builder(10).build()
        val addScreenOperation = ActiveLookOperation.AddScreen(screen)
        val displayScreenOperation = ActiveLookOperation.ShowScreen(screen.id, "Test")

        every { processor.enqueueOperation(any()) } returns Unit

        instance.operationProcessor = processor
        instance.currentSession = session

        assertNotEquals(LoadingMode.LAZY, instance.loadingMode)

        instance.enqueueOperation(addScreenOperation)

        verify(exactly = 1) { processor.enqueueOperation(any()) }

        instance.loadingMode = LoadingMode.LAZY

        assertEquals(LoadingMode.LAZY, instance.loadingMode)

        instance.enqueueOperation(addScreenOperation)

        verify(exactly = 1) { processor.enqueueOperation(any()) }

        instance.enqueueOperation(displayScreenOperation)

        verify(exactly = 3) { processor.enqueueOperation(any()) }
    }

    @Test
    fun testLoadingModeNormal() {
        val instance = ActiveLookSdk.newInstance(appContext)
        val processor = mockk<ActiveLookOperationProcessor>()
        val session = mockk<GattSession>()

        val screen = Screen.Builder(10).build()
        val addScreenOperation = ActiveLookOperation.AddScreen(screen)
        val displayScreenOperation = ActiveLookOperation.ShowScreen(screen.id, "Test")

        every { processor.enqueueOperation(any()) } returns Unit

        instance.operationProcessor = processor
        instance.currentSession = session

        assertEquals(LoadingMode.NORMAL, instance.loadingMode)

        instance.enqueueOperation(addScreenOperation)

        verify(exactly = 1) { processor.enqueueOperation(any()) }

        instance.loadingMode = LoadingMode.NORMAL

        assertEquals(LoadingMode.NORMAL, instance.loadingMode)

        instance.enqueueOperation(addScreenOperation)

        verify(exactly = 2) { processor.enqueueOperation(any()) }

        instance.enqueueOperation(displayScreenOperation)

        verify(exactly = 3) { processor.enqueueOperation(any()) }
    }

}