//package com.dave.spell_checker.ui.main
//
//import androidx.lifecycle.Observer
//import com.dave.spellchecker.network.common.Resource
//import com.dave.spellchecker.ui.main.MainRepository
//import com.dave.spellchecker.ui.main.MainViewModel
//import com.dave.spellchecker.util.Message
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.Mockito.mock
//import org.mockito.Mockito.verify
//import org.mockito.Mockito.`when`
//import org.mockito.junit.MockitoJUnit
//import org.mockito.junit.MockitoRule
//
//@ExperimentalCoroutinesApi
//class MainViewModelTest {
//
//    @get:Rule
//    val mockitoRule: MockitoRule = MockitoJUnit.rule()
//
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()
//
//    @Mock
//    private lateinit var repository: MainRepository
//
//    @Mock
//    private lateinit var htmlResultObserver: Observer<String>
//
//    @Mock
//    private lateinit var toastObserver: Observer<Message>
//
//    private lateinit var viewModel: MainViewModel
//
//    private val dispatcher = TestCoroutineDispatcher()
//
//    @Before
//    fun setup() {
//        viewModel = MainViewModel(repository).also {
//            it.htmlResult.observeForever(htmlResultObserver)
//            it.toast.observeForever(toastObserver)
//        }
//    }
//
//    @Test
//    fun `spellCheck returns correct htmlResult for success`() = dispatcher.runBlockingTest {
//        val query = "This is a test."
//        val pojo = mock(SpellCheckResponse::class.java)
//        `when`(pojo.htmlString).thenReturn("<b>Test</b>")
//        `when`(repository.spellCheck(query)).thenReturn(Resource.Success(pojo))
//
//        viewModel.spellCheck(query)
//
//        verify(htmlResultObserver).onChanged("<b>Test</b>")
//    }
//
//    @Test
//    fun `spellCheck shows toast for error`() = dispatcher.runBlockingTest {
//        val query = "This is a test."
//        val errorMsg = "Error occurred"
//        `when`(repository.spellCheck(query)).thenReturn(Resource.Error(Exception(errorMsg)))
//
//        viewModel.spellCheck(query)
//
//        verify(toastObserver).onChanged(Message.Toast(errorMsg))
//    }
//
//    // 기타 다른 테스트 케이스를 이곳에 추가할 수 있습니다.
//}
