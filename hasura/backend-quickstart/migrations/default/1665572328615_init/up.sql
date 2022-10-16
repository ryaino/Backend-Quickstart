SET check_function_bodies = false;
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';
CREATE TABLE public.assigned_user_roles (
    user_id uuid NOT NULL,
    user_role_name text NOT NULL
);
CREATE TABLE public.user_roles (
    name text NOT NULL
);
INSERT INTO public.user_roles (name) VALUES
    ('ROLE_USER');
CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    email text NOT NULL,
    password text NOT NULL
);
ALTER TABLE ONLY public.assigned_user_roles
    ADD CONSTRAINT assigned_user_roles_pkey PRIMARY KEY (user_id, user_role_name);
ALTER TABLE ONLY public.user_roles
    ADD CONSTRAINT user_roles_pkey PRIMARY KEY (name);
ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);
ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.assigned_user_roles
    ADD CONSTRAINT assigned_user_roles_user_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY public.assigned_user_roles
    ADD CONSTRAINT assigned_user_roles_user_role_fkey FOREIGN KEY (user_role_name) REFERENCES public.user_roles(name) ON UPDATE RESTRICT ON DELETE RESTRICT;
