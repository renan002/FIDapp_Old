package br.com.micdev.fidapp;

public class Config {

    static final String LOGIN_URL = "https://micdev.000webhostapp.com/login.php";
    static final String NOME_URL = "https://micdev.000webhostapp.com/peganome.php?email=";
    static final String DADOS_URL = "https://micdev.000webhostapp.com/pegatudo.php?email=";
    public static final String ID_URL = "https://micdev.000webhostapp.com/pegaid.php?email=";
    static final String EVENTOS_ATIVOS_URL = "https://micdev.000webhostapp.com/getEventosAtivos.php?id_user=";
    static final String EVENTOS_UTILIZADOS_URL = "https://micdev.000webhostapp.com/getEventosUtilizados.php?id_user=";
    static final String EVENTOS_VENCIDOS_URL = "https://micdev.000webhostapp.com/getEventosVencidos.php?id_user=";
    static final String EVENTOS_REVOGADOS_URL = "https://micdev.000webhostapp.com/getEventosRevogados.php?id_user=";
    static final String EVENTOS_INFO_URL = "https://micdev.000webhostapp.com/getEventosInfo.php?nome_evento=";
    static final String EVENTO_ADICIONA_URL = "https://micdev.000webhostapp.com/addEvento.php?cod_evento=renisa1475";
    static final String RESET_SENHA_URL = "https://micdev.000webhostapp.com/resetSenha.php";
    static final String POLITICA_URL = "https://micdev.000webhostapp.com";

    static final String JSON_ARRAY = "result";

    static final String KEY_EMAIL = "email";
    static final String KEY_PASSWORD = "senha";
    static final String KEY_NOME = "nome";
    static final String KEY_IDADE = "idade";
    static final String KEY_ESTADO = "estado";
    static final String KEY_CIDADE = "cidade";
    static final String KEY_ID = "id_user";

    static final String KEY_EVENTO_ID = "id_evento";
    static final String KEY_NOME_EVENTO = "nome_evento";
    static final String KEY_EVENTO_DESCRICAO = "evento_descricao";
    static final String KEY_EVENTO_DATA = "evento_data";
    static final String KEY_EVENTO_LOCAL = "evento_local";
    static final String KEY_VENCE_DIA = "evento_vence_dia";
    static final String KEY_COD_EVENTO = "cod_evento";

    static final String LOGIN_SUCCESS = "success";

    static final String SHARED_PREF_NAME = "myloginapp";

    static final String EMAIL_SHARED_PREF = "email";
    static final String NOME_SHARED_PREF = "nome";
    static final String ID_SHARED_PREF = "id_user";

    static final String LOGGEDIN_SHARED_PREF = "loggedin";
}



