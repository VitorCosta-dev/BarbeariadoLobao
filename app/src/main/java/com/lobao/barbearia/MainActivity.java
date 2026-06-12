package com.lobao.barbearia;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {
    private static final int BLACK = Color.rgb(11, 11, 11);
    private static final int PANEL = Color.rgb(21, 21, 19);
    private static final int GOLD = Color.rgb(210, 173, 79);
    private static final int GOLD_LIGHT = Color.rgb(240, 213, 138);
    private static final int TEXT = Color.rgb(244, 240, 230);
    private static final int MUTED = Color.rgb(170, 162, 143);

    private String selectedService = "Corte masculino";
    private String selectedPrice = "R$ 35";
    private String selectedTime = "13:00";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializa o Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Verifica se o usuário já está logado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            showHome();
        } else {
            showLogin();
        }
    }

    private void showLogin() {
        LinearLayout content = base();
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        addBrand(content, "Barbearia do Lobão", "Seu estilo, nossa arte");
        
        EditText emailInput = addInput(content, "E-mail", "", false);
        EditText senhaInput = addInput(content, "Senha", "", true);
        
        content.addView(primaryButton("Entrar", v -> {
            String email = emailInput.getText().toString().trim();
            String senha = senhaInput.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            showHome();
                        } else {
                            Toast.makeText(this, "Erro ao entrar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }));

        LinearLayout row = row();
        row.addView(outlineButton("Cadastre-se", v -> showSignup()), weightParams());
        row.addView(outlineButton("Área do barbeiro", v -> showAdminLogin()), weightParams());
        content.addView(row);
        setContentView(scroll(content));
    }

    private void showAdminLogin() {
        LinearLayout content = base();
        addTop(content, "Acesso Restrito", this::showLogin);
        
        EditText emailInput = addInput(content, "E-mail do Barbeiro", "", false);
        EditText senhaInput = addInput(content, "Senha", "", true);

        content.addView(primaryButton("Entrar como Barbeiro", v -> {
            String email = emailInput.getText().toString().trim();
            String senha = senhaInput.getText().toString().trim();

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            db.collection("usuarios").document(mAuth.getUid()).get()
                                    .addOnSuccessListener(doc -> {
                                        String tipo = doc.getString("tipo");
                                        if ("barbeiro".equals(tipo) || "admin".equals(tipo)) {
                                            showAdmin();
                                        } else {
                                            mAuth.signOut();
                                            Toast.makeText(this, "Acesso apenas para profissionais", Toast.LENGTH_SHORT).show();
                                            showLogin();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Falha: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }));
        setContentView(scroll(content));
    }

    private void showSignup() {
        LinearLayout content = base();
        addTop(content, "Criar Conta", this::showLogin);

        addPanel(content, panel -> {
            EditText nomeInput = addInput(panel, "Nome completo", "", false);
            EditText emailInput = addInput(panel, "E-mail", "", false);
            EditText senhaInput = addInput(panel, "Senha", "", true);
            EditText confirmarSenhaInput = addInput(panel, "Confirmar senha", "", true);

            panel.addView(primaryButton("Cadastrar Agora", v -> {
                String nome = nomeInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String senha = senhaInput.getText().toString().trim();
                String conf = confirmarSenhaInput.getText().toString().trim();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(this, "Campos obrigatórios vazios", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!senha.equals(conf)) {
                    Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String uid = mAuth.getCurrentUser().getUid();
                                Map<String, Object> user = new HashMap<>();
                                user.put("nome", nome);
                                user.put("email", email);
                                user.put("tipo", "cliente");
                                user.put("criadoEm", FieldValue.serverTimestamp());

                                db.collection("usuarios").document(uid).set(user)
                                        .addOnSuccessListener(aVoid -> showHome());
                            } else {
                                Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }));
        });

        setContentView(scroll(content));
    }

    private void showHome() {
        LinearLayout content = base();
        addTop(content, "Olá!", () -> {
            mAuth.signOut();
            showLogin();
        });
        
        addPanel(content, panel -> {
            panel.setMinimumHeight(dp(120));
            addTitle(panel, "Barbearia do Lobão", 26);
            addSmall(panel, "Seu visual em boas mãos", GOLD_LIGHT);
        });

        addPanel(content, panel -> {
            addSectionTitle(panel, "Meus Compromissos");
            db.collection("agendamentos")
                    .whereEqualTo("usuarioId", mAuth.getUid())
                    .get()
                    .addOnSuccessListener(docs -> {
                        if (docs.isEmpty()) {
                            addSmall(panel, "Nenhum agendamento para hoje.", MUTED);
                        } else {
                            for (QueryDocumentSnapshot doc : docs) {
                                addSummary(panel, doc.getString("servico"), doc.getString("horario"));
                            }
                        }
                    });
        });

        content.addView(primaryButton("Novo Agendamento", v -> showSchedule()));
        setContentView(scroll(content));
    }

    private void showSchedule() {
        LinearLayout content = base();
        addTop(content, "Agendar", this::showHome);
        
        addPanel(content, panel -> {
            addSectionTitle(panel, "Serviço");
            String[][] servicos = {
                {"Corte", "R$ 35"}, {"Barba", "R$ 25"}, {"Combo", "R$ 55"}
            };
            for (String[] s : servicos) {
                panel.addView(outlineButton(s[0] + " - " + s[1], v -> {
                    selectedService = s[0];
                    selectedPrice = s[1];
                    Toast.makeText(this, s[0] + " selecionado", Toast.LENGTH_SHORT).show();
                }));
            }

            addSectionTitle(panel, "Horário");
            String[] horas = {"09:00", "10:30", "14:00", "16:00"};
            LinearLayout row = row();
            for (String h : horas) {
                row.addView(outlineButton(h, v -> {
                    selectedTime = h;
                    Toast.makeText(this, "Às " + h, Toast.LENGTH_SHORT).show();
                }), weightParams());
            }
            panel.addView(row);

            panel.addView(primaryButton("Próximo Passo", v -> showPayment()));
        });
        setContentView(scroll(content));
    }

    private void showPayment() {
        LinearLayout content = base();
        addTop(content, "Resumo", this::showSchedule);

        addPanel(content, panel -> {
            addSummary(panel, "O que:", selectedService);
            addSummary(panel, "Quando:", selectedTime);
            addSummary(panel, "Valor:", selectedPrice);
        });

        content.addView(primaryButton("Confirmar e Salvar", v -> salvarAgendamento()));
        setContentView(scroll(content));
    }

    private void salvarAgendamento() {
        Map<String, Object> ag = new HashMap<>();
        ag.put("usuarioId", mAuth.getUid());
        ag.put("servico", selectedService);
        ag.put("horario", selectedTime);
        ag.put("preco", selectedPrice);
        ag.put("data", FieldValue.serverTimestamp());

        db.collection("agendamentos").add(ag)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Reservado com sucesso!", Toast.LENGTH_SHORT).show();
                    showConfirmed();
                });
    }

    private void showConfirmed() {
        LinearLayout content = base();
        content.setGravity(Gravity.CENTER);
        addTitle(content, "Tudo certo!", 32);
        addSmall(content, "Te esperamos às " + selectedTime, GOLD);
        content.addView(primaryButton("Voltar", v -> showHome()));
        setContentView(scroll(content));
    }

    private void showAdmin() {
        LinearLayout content = base();
        addTop(content, "Gestão", this::showLogin);
        
        addPanel(content, panel -> {
            addSectionTitle(panel, "Agenda de Hoje");
            db.collection("agendamentos").orderBy("horario").get()
                    .addOnSuccessListener(docs -> {
                        for (QueryDocumentSnapshot doc : docs) {
                            addSummary(panel, doc.getString("horario"), doc.getString("servico"));
                        }
                    });
        });

        content.addView(primaryButton("Sair do Painel", v -> {
            mAuth.signOut();
            showLogin();
        }));
        setContentView(scroll(content));
    }

    // --- MÉTODOS AUXILIARES DE UI (Traduzidos e Otimizados) ---

    private LinearLayout base() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(dp(20), dp(20), dp(20), dp(20));
        l.setBackgroundColor(BLACK);
        return l;
    }

    private ScrollView scroll(View content) {
        ScrollView s = new ScrollView(this);
        s.setFillViewport(true);
        s.addView(content);
        return s;
    }

    private void addBrand(LinearLayout p, String t, String s) {
        TextView icon = text("✂", 50, GOLD, Typeface.BOLD);
        icon.setGravity(Gravity.CENTER);
        p.addView(icon);
        addTitle(p, t, 30);
        TextView sub = text(s.toUpperCase(), 12, MUTED, Typeface.NORMAL);
        sub.setGravity(Gravity.CENTER);
        p.addView(sub);
        p.addView(space(30));
    }

    private void addTop(LinearLayout p, String t, Runnable back) {
        LinearLayout r = row();
        Button b = outlineButton("‹", v -> back.run());
        r.addView(b, new LinearLayout.LayoutParams(dp(45), dp(45)));
        TextView l = text(t, 20, GOLD, Typeface.BOLD);
        l.setPadding(dp(15), 0, 0, 0);
        r.addView(l);
        p.addView(r);
        p.addView(space(20));
    }

    private void addPanel(LinearLayout p, PanelBuilder b) {
        LinearLayout pan = new LinearLayout(this);
        pan.setOrientation(LinearLayout.VERTICAL);
        pan.setPadding(dp(15), dp(15), dp(15), dp(15));
        pan.setBackgroundResource(R.drawable.panel);
        b.build(pan);
        LinearLayout.LayoutParams lp = fullParams();
        lp.setMargins(0, 0, 0, dp(15));
        p.addView(pan, lp);
    }

    private EditText addInput(LinearLayout p, String l, String v, boolean pass) {
        p.addView(text(l, 12, MUTED, Typeface.BOLD));
        EditText i = new EditText(this);
        i.setText(v);
        i.setTextColor(TEXT);
        i.setBackgroundResource(R.drawable.input);
        i.setPadding(dp(10), dp(10), dp(10), dp(10));
        if (pass) i.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams lp = fullParams();
        lp.setMargins(0, dp(5), 0, dp(15));
        p.addView(i, lp);
        return i;
    }

    private void addSummary(LinearLayout p, String l, String v) {
        LinearLayout r = row();
        r.addView(text(l, 15, MUTED, Typeface.NORMAL), weightParams());
        TextView rv = text(v, 15, GOLD_LIGHT, Typeface.BOLD);
        rv.setGravity(Gravity.END);
        r.addView(rv, weightParams());
        p.addView(r);
        p.addView(space(8));
    }

    private void addTitle(LinearLayout p, String t, int s) {
        TextView tv = text(t, s, GOLD, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        p.addView(tv, fullParams());
    }

    private void addSectionTitle(LinearLayout p, String t) {
        TextView tv = text(t, 18, TEXT, Typeface.BOLD);
        p.addView(tv);
        p.addView(space(10));
    }

    private void addSmall(LinearLayout p, String v, int c) {
        TextView tv = text(v, 14, c, Typeface.NORMAL);
        tv.setGravity(Gravity.CENTER);
        p.addView(tv, fullParams());
    }

    private Button primaryButton(String l, View.OnClickListener cl) {
        Button b = new Button(this);
        b.setText(l);
        b.setBackgroundResource(R.drawable.button_gold);
        b.setOnClickListener(cl);
        return b;
    }

    private Button outlineButton(String l, View.OnClickListener cl) {
        Button b = new Button(this);
        b.setText(l);
        b.setTextColor(GOLD_LIGHT);
        b.setBackgroundResource(R.drawable.button_outline);
        b.setOnClickListener(cl);
        return b;
    }

    private TextView text(String v, int s, int c, int st) {
        TextView tv = new TextView(this);
        tv.setText(v);
        tv.setTextSize(s);
        tv.setTextColor(c);
        tv.setTypeface(Typeface.DEFAULT, st);
        return tv;
    }

    private LinearLayout row() {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        return r;
    }

    private Space space(int h) {
        Space s = new Space(this);
        s.setLayoutParams(new LinearLayout.LayoutParams(1, dp(h)));
        return s;
    }

    private LinearLayout.LayoutParams fullParams() {
        return new LinearLayout.LayoutParams(-1, -2);
    }

    private LinearLayout.LayoutParams weightParams() {
        return new LinearLayout.LayoutParams(0, -2, 1);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + .5f);
    }

    private interface PanelBuilder { void build(LinearLayout p); }
}