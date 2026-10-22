package com.workaudit.workaudit.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Keeps existing PostgreSQL installations compatible when AuditAction gains a new value.
 * Hibernate may have generated a CHECK constraint containing only the enum values that
 * existed when audit_logs was first created. The Java enum remains the source of truth.
 */
@Configuration
public class AuditLogSchemaCompatibility {

    @Bean
    ApplicationRunner removeStaleAuditActionCheckConstraints(JdbcTemplate jdbcTemplate) {
        return args -> jdbcTemplate.execute("""
                DO $$
                DECLARE constraint_row RECORD;
                BEGIN
                    IF to_regclass('public.audit_logs') IS NOT NULL THEN
                        FOR constraint_row IN
                            SELECT conname
                            FROM pg_constraint
                            WHERE conrelid = 'public.audit_logs'::regclass
                              AND contype = 'c'
                              AND pg_get_constraintdef(oid) ILIKE '%action%'
                        LOOP
                            EXECUTE format('ALTER TABLE public.audit_logs DROP CONSTRAINT %I',
                                           constraint_row.conname);
                        END LOOP;
                    END IF;
                END $$;
                """);
    }
}
