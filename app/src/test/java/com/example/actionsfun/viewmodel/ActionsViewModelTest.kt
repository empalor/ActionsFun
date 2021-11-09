package com.example.actionsfun.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.actionsfun.model.Action
import com.example.actionsfun.repository.MockActionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class ActionsViewModelTest {

    @Rule
    @JvmField
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    var initRule: MockitoRule = MockitoJUnit.rule()

    private val application = Mockito.mock(Application::class.java)

    private val actionsMockRepository: MockActionsRepository =
        Mockito.mock(MockActionsRepository::class.java)

    private lateinit var actionsViewModel: ActionsViewModel

    private lateinit var actions: ArrayList<Action>

    val dispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        actions = ArrayList()
        actions.add(Action(1, "animation", false, 10, listOf(0, 1, 2), 1000L))
        actions.add(Action(2, "toast", true, 9, listOf(3, 4, 5), 1000L))
        actions.add(Action(3, "call", false, 11, listOf(0, 1, 6), 1000L))
        actions.add(Action(4, "notification", true, 7, listOf(2, 4, 6), 1000L))

        actionsViewModel = ActionsViewModel(MockActionsRepository())
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun isDataFetched() {
        runBlockingTest {
            Thread.sleep(500)
            val data = actionsViewModel.actionList.value?.data
            data?.results?.let { assert(it.isNotEmpty()) }
        }
    }

    @Test
    fun isActionReturnedAfterButtonIsClicked() {
        runBlockingTest {
            Thread.sleep(1000)
            val action = actionsViewModel.onActionButtonClicked()
            assert(action != null)
        }
    }
}