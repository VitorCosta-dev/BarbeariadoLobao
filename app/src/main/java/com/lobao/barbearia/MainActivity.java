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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLogin();
    }

    private void showLogin() {
        LinearLayout content = base();
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        addBrand(content, "Barbearia do Lobão", "Seu estilo, nossa arte");
        addInput(content, "E-mail", "cliente@lobao.com", false);
        addInput(content, "Senha", "123456", true);
        content.addView(primaryButton("Entrar", v -> showHome()));
        LinearLayout row = row();
        row.addView(outlineButton("Cadastre-se", v -> showSignup()), weightParams());
        row.addView(outlineButton("Área do barbeiro", v -> showAdmin()), weightParams());
        content.addView(row);
        setContentView(scroll(content));
    }

    private void showSignup() {
        LinearLayout content = base();
        addTop(content, "Cadastro", this::showLogin);
        addPanel(content, panel -> {
            addInput(panel, "Barbearia", "Barbearia do Lobão", false);
            addInput(panel, "Nome completo", "", false);
            addInput(panel, "Telefone", "", false);
            addInput(panel, "E-mail", "", false);
            addInput(panel, "Senha", "", true);
            addInput(panel, "Confirmar senha", "", true);
            panel.addView(primaryButton("Criar conta", v -> showHome()));
        });
        setContentView(scroll(content));
    }

    private void showHome() {
        LinearLayout content = base();
        addTop(content, "Barbearia do Lobão", this::showLogin);
        addPanel(content, panel -> {
            panel.setMinimumHeight(dp(170));
            addSmall(panel, "Aberto hoje até 20:00", MUTED);
            addTitle(panel, "Corte, barba e acabamento", 25);
            addSmall(panel, "Rua Principal, 120  |  Pagamento no app  |  Agenda online", GOLD_LIGHT);
        });
        addPanel(content, panel -> {
            addSectionTitle(panel, "Serviços");
            addPrice(panel, "Corte masculino", "R$ 35");
            addPrice(panel, "Barba desenhada", "R$ 25");
            addPrice(panel, "Corte + barba", "R$ 55");
            addPrice(panel, "Sobrancelha", "R$ 15");
        });
        content.addView(primaryButton("Agendar horário", v -> showSchedule()));
        setContentView(scroll(content));
    }

    private void showSchedule() {
        LinearLayout content = base();
        addTop(content, "Agendamento", this::showHome);
        addPanel(content, panel -> {
            addSectionTitle(panel, "Escolha o serviço");
            addChoice(panel, "Corte masculino", "R$ 35");
            addChoice(panel, "Barba desenhada", "R$ 25");
            addChoice(panel, "Corte + barba", "R$ 55");
            addChoice(panel, "Sobrancelha", "R$ 15");
            addSectionTitle(panel, "Horário");
            LinearLayout times = row();
            times.addView(timeButton("09:00"), weightParams());
            times.addView(timeButton("10:30"), weightParams());
            times.addView(timeButton("13:00"), weightParams());
            panel.addView(times);
            LinearLayout times2 = row();
            times2.addView(timeButton("14:30"), weightParams());
            times2.addView(timeButton("16:00"), weightParams());
            times2.addView(timeButton("18:30"), weightParams());
            panel.addView(times2);
            panel.addView(primaryButton("Continuar", v -> showPayment()));
        });
        setContentView(scroll(content));
    }

    private void showPayment() {
        LinearLayout content = base();
        addTop(content, "Pagamento", this::showSchedule);
        addPanel(content, panel -> {
            addSummary(panel, "Barbearia", "Barbearia do Lobão");
            addSummary(panel, "Serviço", selectedService);
            addSummary(panel, "Horário", selectedTime);
            addSummary(panel, "Total", selectedPrice);
        });
        addPanel(content, panel -> {
            addSectionTitle(panel, "Forma de pagamento");
            addSmall(panel, "Pix selecionado", GOLD_LIGHT);
            panel.addView(primaryButton("Confirmar agendamento", v -> showConfirmed()));
        });
        setContentView(scroll(content));
    }

    private void showConfirmed() {
        LinearLayout content = base();
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        addBrand(content, "Confirmado", "Barbearia do Lobão");
        addPanel(content, panel -> {
            addSummary(panel, "Status", "Agendado");
            addSummary(panel, "Horário", selectedTime);
            addSummary(panel, "Pagamento", "Recebido");
        });
        content.addView(primaryButton("Voltar ao início", v -> showHome()));
        setContentView(scroll(content));
    }

    private void showAdmin() {
        LinearLayout content = base();
        addTop(content, "Gestão", this::showLogin);
        addPanel(content, panel -> {
            addSectionTitle(panel, "Barbearia do Lobão");
            addSmall(panel, "Agenda do dia, pausas, pagamentos e configuração da barbearia.", MUTED);
            addSummary(panel, "Agendados hoje", "12 horários");
            addSummary(panel, "Recebido no app", "R$ 420");
            addSummary(panel, "Pausa", "15:00 às 15:30");
            addSummary(panel, "Avaliação média", "4,9");
        });
        addPanel(content, panel -> {
            addSectionTitle(panel, "Próximos atendimentos");
            addSummary(panel, "13:00", "Lucas");
            addSummary(panel, "14:30", "Renato");
            addSummary(panel, "16:00", "Marcos");
        });
        setContentView(scroll(content));
    }

    private LinearLayout base() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(22), dp(28), dp(22), dp(28));
        layout.setBackgroundColor(BLACK);
        return layout;
    }

    private ScrollView scroll(View content) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BLACK);
        scroll.addView(content);
        return scroll;
    }

    private void addBrand(LinearLayout parent, String title, String subtitle) {
        TextView mark = new TextView(this);
        mark.setText("✂");
        mark.setTextColor(GOLD);
        mark.setTextSize(54);
        mark.setGravity(Gravity.CENTER);
        parent.addView(mark, fullParams());
        addTitle(parent, title, 32);
        TextView sub = text(subtitle.toUpperCase(), 12, MUTED, Typeface.BOLD);
        sub.setGravity(Gravity.CENTER);
        sub.setLetterSpacing(.18f);
        parent.addView(sub, fullParams());
        parent.addView(space(28));
    }

    private void addTop(LinearLayout parent, String title, Runnable back) {
        LinearLayout top = row();
        Button backButton = outlineButton("‹", v -> back.run());
        top.addView(backButton, new LinearLayout.LayoutParams(dp(48), dp(48)));
        TextView label = text(title, 22, GOLD, Typeface.BOLD);
        label.setGravity(Gravity.CENTER_VERTICAL);
        top.addView(label, weightParams());
        parent.addView(top, fullParams());
        parent.addView(space(18));
    }

    private void addPanel(LinearLayout parent, PanelBuilder builder) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(14), dp(14), dp(14), dp(14));
        panel.setBackgroundResource(R.drawable.panel);
        builder.build(panel);
        LinearLayout.LayoutParams params = fullParams();
        params.setMargins(0, 0, 0, dp(14));
        parent.addView(panel, params);
    }

    private void addInput(LinearLayout parent, String label, String value, boolean password) {
        TextView tv = text(label.toUpperCase(), 12, MUTED, Typeface.BOLD);
        parent.addView(tv, fullParams());
        EditText input = new EditText(this);
        input.setText(value);
        input.setTextColor(TEXT);
        input.setHintTextColor(MUTED);
        input.setTextSize(16);
        input.setSingleLine(true);
        input.setPadding(dp(12), 0, dp(12), 0);
        input.setBackgroundResource(R.drawable.input);
        if (password) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        LinearLayout.LayoutParams params = fullParams();
        params.setMargins(0, dp(7), 0, dp(16));
        parent.addView(input, params);
    }

    private void addChoice(LinearLayout parent, String name, String price) {
        Button btn = outlineButton(name + "  " + price, v -> {
            selectedService = name;
            selectedPrice = price;
            showSchedule();
        });
        parent.addView(btn, fullParams());
    }

    private Button timeButton(String time) {
        return outlineButton(time, v -> {
            selectedTime = time;
            showSchedule();
        });
    }

    private void addPrice(LinearLayout parent, String label, String price) {
        addSummary(parent, label, price);
    }

    private void addSummary(LinearLayout parent, String label, String value) {
        LinearLayout row = row();
        TextView left = text(label, 15, MUTED, Typeface.NORMAL);
        TextView right = text(value, 15, GOLD_LIGHT, Typeface.BOLD);
        right.setGravity(Gravity.RIGHT);
        row.addView(left, weightParams());
        row.addView(right, weightParams());
        parent.addView(row, fullParams());
        parent.addView(space(10));
    }

    private void addTitle(LinearLayout parent, String title, int size) {
        TextView tv = text(title, size, GOLD, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        parent.addView(tv, fullParams());
    }

    private void addSectionTitle(LinearLayout parent, String title) {
        TextView tv = text(title, 19, TEXT, Typeface.BOLD);
        LinearLayout.LayoutParams params = fullParams();
        params.setMargins(0, 0, 0, dp(10));
        parent.addView(tv, params);
    }

    private void addSmall(LinearLayout parent, String value, int color) {
        TextView tv = text(value, 14, color, Typeface.NORMAL);
        tv.setLineSpacing(2, 1.1f);
        parent.addView(tv, fullParams());
    }

    private Button primaryButton(String label, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(Color.rgb(18, 16, 11));
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setAllCaps(true);
        button.setBackgroundResource(R.drawable.button_gold);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = fullParams();
        params.setMargins(0, dp(8), 0, dp(8));
        button.setLayoutParams(params);
        return button;
    }

    private Button outlineButton(String label, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(GOLD_LIGHT);
        button.setTextSize(13);
        button.setAllCaps(false);
        button.setBackgroundResource(R.drawable.button_outline);
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams params = fullParams();
        params.setMargins(0, dp(6), 0, dp(6));
        button.setLayoutParams(params);
        return button;
    }

    private TextView text(String value, int size, int color, int style) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);
        tv.setTypeface(Typeface.DEFAULT, style);
        return tv;
    }

    private LinearLayout row() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 0, 0, 0);
        return row;
    }

    private Space space(int height) {
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(1, dp(height)));
        return space;
    }

    private LinearLayout.LayoutParams fullParams() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams weightParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        params.setMargins(dp(4), 0, dp(4), 0);
        return params;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + .5f);
    }

    private interface PanelBuilder {
        void build(LinearLayout panel);
    }
}
