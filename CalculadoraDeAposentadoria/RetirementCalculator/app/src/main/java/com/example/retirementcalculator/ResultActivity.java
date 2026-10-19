package com.example.retirementcalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    // Regras de aposentadoria
    private static final int IDADE_MINIMA_HOMEM = 65;
    private static final int CONTRIBUICAO_MINIMA_HOMEM = 20;
    private static final int IDADE_MINIMA_MULHER = 62;
    private static final int CONTRIBUICAO_MINIMA_MULHER = 15;

    private TextView txtNome;
    private TextView txtIdade;
    private TextView txtStatus;
    private TextView txtAnosRestantes;

    private String mensagemCompartilhamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        txtNome = findViewById(R.id.txtNome);
        txtIdade = findViewById(R.id.txtIdade);
        txtStatus = findViewById(R.id.txtStatus);
        txtAnosRestantes = findViewById(R.id.txtAnosRestantes);
        Button btnCompartilhar = findViewById(R.id.btnCompartilhar);

        Intent intent = getIntent();
        String nome = intent.getStringExtra(MainActivity.EXTRA_NOME);
        String sexo = intent.getStringExtra(MainActivity.EXTRA_SEXO);
        int idade = intent.getIntExtra(MainActivity.EXTRA_IDADE, 0);
        int contribuicao = intent.getIntExtra(MainActivity.EXTRA_CONTRIBUICAO, 0);

        processarResultado(nome, sexo, idade, contribuicao, btnCompartilhar);

        btnCompartilhar.setOnClickListener(v -> compartilhar());
    }

    private void processarResultado(String nome, String sexo, int idade, int contribuicao, Button btnCompartilhar) {
        boolean sexoValido = "M".equalsIgnoreCase(sexo) || "F".equalsIgnoreCase(sexo);

        if (!sexoValido) {
            txtNome.setText("Nome: " + nome);
            txtIdade.setText("Idade: " + idade + " anos");
            txtStatus.setText("ERRO: o valor \"" + sexo + "\" informado para sexo é inválido. Digite apenas M ou F.");
            txtAnosRestantes.setVisibility(android.view.View.GONE);

            mensagemCompartilhamento = "Não foi possível calcular a aposentadoria de " + nome
                    + " pois o sexo informado (\"" + sexo + "\") é inválido.";
            btnCompartilhar.setEnabled(false);
            return;
        }

        int idadeMinima = "M".equalsIgnoreCase(sexo) ? IDADE_MINIMA_HOMEM : IDADE_MINIMA_MULHER;
        int contribuicaoMinima = "M".equalsIgnoreCase(sexo) ? CONTRIBUICAO_MINIMA_HOMEM : CONTRIBUICAO_MINIMA_MULHER;

        boolean podeAposentar = idade >= idadeMinima && contribuicao >= contribuicaoMinima;

        txtNome.setText("Nome: " + nome);
        txtIdade.setText("Idade: " + idade + " anos");

        if (podeAposentar) {
            txtStatus.setText("PARABÉNS, você pode aposentar!");
            txtAnosRestantes.setVisibility(android.view.View.GONE);

            mensagemCompartilhamento = String.format(
                    "%s pode se aposentar pois tem %d anos de idade e %d anos de contribuição!",
                    nome, idade, contribuicao);
        } else {
            txtStatus.setText("Ainda não pode aposentar agora!");

            int anosFaltantesIdade = Math.max(0, idadeMinima - idade);
            int anosFaltantesContribuicao = Math.max(0, contribuicaoMinima - contribuicao);
            int anosRestantes = Math.max(anosFaltantesIdade, anosFaltantesContribuicao);

            txtAnosRestantes.setVisibility(android.view.View.VISIBLE);
            txtAnosRestantes.setText("Faltam " + anosRestantes + " ano(s) para poder se aposentar.");

            mensagemCompartilhamento = String.format(
                    "%s ainda não pode se aposentar. Tem %d anos de idade e %d anos de contribuição. Faltam %d ano(s) para se aposentar.",
                    nome, idade, contribuicao, anosRestantes);
        }
    }

    private void compartilhar() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mensagemCompartilhamento);

        Intent chooser = Intent.createChooser(shareIntent, "Compartilhar resultado via");

        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            // Nenhum app de compartilhamento disponível: copia a mensagem para a área de transferência
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Resultado da aposentadoria", mensagemCompartilhamento);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Nenhum app de compartilhamento encontrado. Mensagem copiada para a área de transferência.", Toast.LENGTH_LONG).show();
        }
    }
}
