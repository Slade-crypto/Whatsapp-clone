package com.example.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.R;
import com.example.whatsapp.config.ConfiguracaoFirebase;
import com.example.whatsapp.helper.Base64Custom;
import com.example.whatsapp.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);

    }

    public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
        usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuario", Toast.LENGTH_SHORT).show();
                    finish();

                    try{

                        String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        usuario.setId(identificadorUsuario);
                        usuario.salvar();

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha mais forte!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, digite um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta ja foi cadastrada";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuario: " + e.getMessage();
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void validarUsuario(View view){
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString( );

        if(!textoNome.isEmpty()){ //verifica nome
            if(!textoEmail.isEmpty()){//verifica email
                if(!textoSenha.isEmpty()){//verifica senha

                    Usuario usuario = new Usuario();
                    usuario.setNome(textoNome);
                    usuario.setEmail(textoEmail);
                    usuario.setSenha(textoSenha);

                    cadastrarUsuario(usuario);


                }else{
                    Toast.makeText(this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, "Preencha o email", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Preencha o nome", Toast.LENGTH_SHORT).show();
        }
    }

}
