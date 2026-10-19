package com.example.retirementcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_NOME = "extra_nome";
    public static final String EXTRA_SEXO = "extra_sexo";
    public static final String EXTRA_IDADE = "extra_idade";
    public static final String EXTRA_CONTRIBUICAO = "extra_contribuicao";

    private EditText editNome;
    private EditText editSexo;
    private EditText editIdade;
    private EditText editContribuicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editNome = findViewById(R.id.editNome);
        editSexo = findViewById(R.id.editSexo);
        editIdade = findViewById(R.id.editIdade);
        editContribuicao = findViewById(R.id.editContribuicao);

        Button btnCalcular = findViewById(R.id.btnCalcular);
        btnCalcular.setOnClickListener(v -> calcular());
    }

    private void calcular() {
        String nome = editNome.getText().toString().trim();
        String sexo = editSexo.getText().toString().trim();
        String idadeStr = editIdade.getText().toString().trim();
        String contribuicaoStr = editContribuicao.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            editNome.setError("Informe o nome");
            editNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(idadeStr)) {
            editIdade.setError("Informe a idade");
            editIdade.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contribuicaoStr)) {
            editContribuicao.setError("Informe o tempo de contribuição");
            editContribuicao.requestFocus();
            return;
        }

        int idade;
        int contribuicao;
        try {
            idade = Integer.parseInt(idadeStr);
            contribuicao = Integer.parseInt(contribuicaoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Idade e tempo de contribuição devem ser números válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idade <= 0 || idade > 130) {
            editIdade.setError("Idade inválida");
            editIdade.requestFocus();
            return;
        }

        if (contribuicao < 0 || contribuicao > idade) {
            editContribuicao.setError("Tempo de contribuição inválido");
            editContribuicao.requestFocus();
            return;
        }

        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra(EXTRA_NOME, nome);
        intent.putExtra(EXTRA_SEXO, sexo.toUpperCase());
        intent.putExtra(EXTRA_IDADE, idade);
        intent.putExtra(EXTRA_CONTRIBUICAO, contribuicao);
        startActivity(intent);
        finish();
    }
}
