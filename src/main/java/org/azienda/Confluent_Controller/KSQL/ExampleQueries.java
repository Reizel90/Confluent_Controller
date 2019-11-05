package org.azienda.Confluent_Controller.KSQL;

import org.azienda.Confluent_Controller.MainClass;

public class ExampleQueries {


    private static String ksql_ddl_head_cmd = String.format(""
            + "curl -X POST http://" + MainClass.connection + ":8088/ksql" + "\n"
            + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" " + "\n"
            + " -d '{\"ksql\":\""
    );

    private static String ksql_ddl_tail_cmd =  (""
            + "\", \"streamsProperties\": {"
                                        + "\"ksql.streams.auto.offset.reset\": \"earliest\""
                                        + "}" +
            "}'"
    );

    private static String createcmd = String.format(""
            + ksql_ddl_head_cmd

            + "\n"
            + " CREATE STREAM <nome_Stream> AS SELECT * \nFROM pageviews_original \nWHERE pageid='home';" + "\n"
            + "\n"
            + " CREATE STREAM pageviews_alice AS SELECT * \nFROM pageviews_original WHERE userid='alice'; " + "\n"
            + "\n"
            + "CREATE STREAM dbo_DIPENDENTI\n" +
            "            (ID BIGINT,\n" +
            "             IDPERSONA BIGINT)\n" +
            "    WITH (KAFKA_TOPIC='REIZEL90.dbo.DIPENDENTI',\n" +
            "          VALUE_FORMAT='DELIMITED',\n" +
            "          KEY='ID');\n"
            + "\n"
            + " CREATE TABLE pageviews_home_count AS SELECT userid, COUNT(*) \nFROM pageviews_home \nGROUP BY userid;" + "\n" + "\n"
            + "\n"

            + ksql_ddl_tail_cmd
    );

    private static String dropcmd = String.format(""
            + "DROP STREAM TABLE <nome_stream_o_table>; "
    );


    private static String terminatecmd = String.format(""
            + "TERMINATE <query_id>; "
    );


    // funziona
    private static String topic_to_table = String.format(""

            + "\n"
            + "-- Goal: You want to create a table from a topic, which is keyed by userid of type INT. \n" +
            "-- Problem: The message key is not present as a field/column in the topic's message values. \n" +
            "-- Create a stream on the original topic.\n" +
            "-- The topic is keyed by userid, which is available as the implicit column ROWKEY\n" +
            "-- in the users_with_missing_key stream. Note how the explicit column definitions\n" +
            "-- only define username and email but not userid.\n" +
            "\n" +
            "CREATE STREAM NOKEY_PERSONA (\n" +
            "      AFTER STRUCT <\n" +
            "         ID BIGINT,\n" +
            "         CODICE BIGINT,\n" +
            "         DESCR1 VARCHAR \n" +
            "         >,\n" +
            "      username VARCHAR, \n" +
            "      email VARCHAR)\n" +
            "      WITH (KAFKA_TOPIC='REIZEL90.dbo.PERSONE', VALUE_FORMAT='JSON');\n" +
            "\n" +
            "\n" +
            "-- Derive a new stream with the required key changes.\n" +
            "-- 1) The contents of ROWKEY (message key) are copied into the message value as the userid_string column,\n" +
            "--    and the CAST statement converts the key to the required format.\n" +
            "-- 2) The PARTITION BY clause re-partitions the stream based on the new, converted key.\n" +
            "\n" +
            "      CREATE STREAM KEY_PERSONA\n" +
            "      WITH(KAFKA_TOPIC='KEY_PERSONA') AS\n" +
            "      SELECT CAST(ROWKEY as VARCHAR) as persona_id_string, AFTER->ID ID, AFTER->CODICE CODICE, AFTER->DESCR1 DESCR1\n" +
            "      FROM NOKEY_PERSONA\n" +
            "      PARTITION BY persona_id_string;\n" +
            "\n" +
            "\n" +
            "-- Now you can create the table on the properly keyed stream.\n" +
            "\n" +
            "      CREATE TABLE PERSONA_table (persona_id_string VARCHAR, ID BIGINT, CODICE BIGINT, DESCR1 VARCHAR)\n" +
            "      WITH (KAFKA_TOPIC='KEY_PERSONA',\n" +
            "            VALUE_FORMAT='JSON',\n" +
            "            KEY='persona_id_string');\n"

    );

    // pericolo

    private static String ksql_select_head_cmd = String.format(""
            + "curl -X POST http://" + MainClass.connection + ":8088/query" + "\n"
            + " -H \"Content-Type: application/vnd.ksql.v1+json; charset=utf-8\" " + "\n"
            + " -d '{\"ksql\": \""
    );

    private static String ksql_select_tail_cmd = String.format(""
            + "\", \"streamsProperties\": {" + "\n"
            + "\"ksql.streams.auto.offset.reset\": \"earliest\"" + "\n"
            + "}" + "\n"
            + "}'"
    );

    private static String selectcmd = String.format(""
            + "\n da non fare perchè non funziona e rischia di rompere tutto \n"
            + " al massimo ricordasi di usare LIMIT e tenersi bassi \n"
            + "\n"

            + " SELECT * \n"
            + "FROM key_dipendente d\n"
            + "LEFT JOIN persona_table p ON d.persona_id = p.persona_id \n"
            + "LIMIT 10;" + "\n" + "\n"

    );


    public static String getKsql_ddl_head_cmd(){
        return ksql_ddl_head_cmd;
    }

    public static String getKsql_ddl_tail_cmd(){
        return ksql_ddl_tail_cmd;
    }

    public static String getKsql_select_head_cmd(){ return ksql_select_head_cmd;}

    public static String getKsql_select_tail_cmd() { return ksql_select_tail_cmd;}

    public static String getCreatecmd(){
        return createcmd;
    }

    public static String getSelectcmd(){
        return selectcmd;
    }

    public static String getDropcmd(){
        return dropcmd;
    }

    public static String getTerminatecmd(){
        return terminatecmd;
    }

    public static String getTopic_to_table(){
        return topic_to_table;
    }






}
