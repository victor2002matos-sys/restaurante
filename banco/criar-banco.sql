-- Cria o usuário e o banco do sistema no PostgreSQL.
-- Rode como o superusuário "postgres" (o criar-banco.bat faz isso no Windows):
--   psql -U postgres -h localhost -f banco/criar-banco.sql
-- Pode rodar mais de uma vez: o que já existe não é recriado.
-- As tabelas são criadas pelo próprio sistema (JPA/Hibernate) na primeira execução.

SELECT 'CREATE ROLE restaurante LOGIN PASSWORD ''restaurante'''
WHERE NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'restaurante')\gexec

SELECT 'CREATE DATABASE restaurante OWNER restaurante ENCODING ''UTF8'' TEMPLATE template0'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'restaurante')\gexec

\echo 'Pronto: banco "restaurante" e usuario "restaurante" (senha "restaurante") disponiveis.'
