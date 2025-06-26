package br.com.mindminers.pesquisas

import android.app.AlertDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.mindminers.pesquisas.models.Question
import br.com.mindminers.pesquisas.ui.theme.MindMinersPesquisasTheme
import br.com.mindminers.pesquisas.viewmodels.QuestionViewModel

class MainActivity : ComponentActivity() {

    private val questionViewModel: QuestionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MindMinersPesquisasTheme { // Use o nome do seu tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuestionListScreen(questionViewModel){index, question ->
                        showPopup(index, question)
                    }
                }
            }
        }
    }

    private fun showPopup(index: Int, question: Question) {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Início da pesquisa")
        alert.setMessage("${question.title} $index")
        alert.setPositiveButton("OK, entendi"){ dialog, _ ->
            dialog.dismiss()
        }
        alert.show()
    }
}

@Composable
fun QuestionListScreen(
    viewModel: QuestionViewModel,
    onClick: (index: Int, question: Question) -> Unit
) {
    val questions by viewModel.questions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(questions) { index, question ->
                QuestionItem(
                    index = index + 1,
                    question = question,
                ){ idx, quest ->
                    onClick(idx, quest)
                }
                // Lógica para carregar mais itens quando o usuário rolar perto do final
                if (index == questions.size - 1 && !isLoading) {
                    viewModel.loadMoreQuestions()
                }
            }

            // Indicador de carregamento no final da lista
            if (isLoading) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItem(index: Int,
                 question: Question,
                 onClick: (index: Int, question: Question) -> Unit) {
    Column(
        modifier = Modifier
            .background(
                color = when (isSystemInDarkTheme()) {
                    true -> Color.Gray
                    else -> Color.LightGray
                },
                shape = RoundedCornerShape(16.dp), // Aplica a cor e a forma de canto arredondado
            )
            .alpha(0.9f)
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "${question.title} $index",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Tempo estimado: ${question.time}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(text = "Recompensa: ${question.reward}")
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = {
                onClick(index, question)
            }) {
                Text("Responder")
            }
        }
    }


}

@Preview(showBackground = true)
@Composable
fun QuestionListScreenPreview() {
    MindMinersPesquisasTheme {
        // Exemplo de como você poderia usar o preview (com dados mockados)
        // Normalmente você não pré-visualiza a tela inteira com o ViewModel real
        // mas sim componentes menores.
        // Para este exemplo, apenas um item.
        QuestionItem(
            index = 1,
            question = Question(
                id = 1,
                title = "What is your favorite color?",
                time = "10 minutos",
                reward = "5 pontos",
            ),
            onClick = {index, question ->  }
        )
    }
}