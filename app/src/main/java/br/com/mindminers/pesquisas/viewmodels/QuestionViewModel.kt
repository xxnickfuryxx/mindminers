package br.com.mindminers.pesquisas.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mindminers.pesquisas.models.Question
import br.com.mindminers.pesquisas.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuestionViewModel : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var currentPage = 0
    private val pageSize = 5
    private var hasMoreItems = true // Para controlar se ainda há mais itens para carregar

    init {
        loadMoreQuestions()
    }

    fun loadMoreQuestions() {
        if (_isLoading.value || !hasMoreItems) return

        _isLoading.value = true
        _errorMessage.value = null // Limpa qualquer erro anterior

        viewModelScope.launch {
            try {
                val newQuestions = RetrofitClient.apiService.getQuestions(
                    offset = currentPage * pageSize,
                    limit = pageSize
                )
                if (newQuestions.isNotEmpty()) {
                    _questions.value = _questions.value + newQuestions
                    currentPage++
                } else {
                    hasMoreItems = false // Não há mais itens para carregar
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar pesquisas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}