-- ============================================================
--  Game Collection App — Database Schema
--  Engine: MySQL 8+
--
--  Usage:
--    mysql -u <user> -p < database/schema.sql
--  or paste into your MySQL client after selecting the target database.
-- ============================================================

-- Use utf8mb4 so Turkish characters and emoji are stored correctly.
SET NAMES utf8mb4;

-- ------------------------------------------------------------
-- Users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kullanicilar (
    id            INT          NOT NULL AUTO_INCREMENT,
    kullanici_adi VARCHAR(100) NOT NULL,
    sifre         VARCHAR(255) NOT NULL,          -- NOTE: currently stored as plain text; will be replaced by a BCrypt hash.
    is_admin      BOOLEAN      NOT NULL DEFAULT FALSE,
    kayit_tarihi  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_kullanici_adi (kullanici_adi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Games (each row belongs to a user)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS oyunlar (
    id           INT          NOT NULL AUTO_INCREMENT,
    kullanici_id INT          NOT NULL,
    title        VARCHAR(255) NOT NULL,
    genre        VARCHAR(255),
    developer    VARCHAR(255),
    publisher    VARCHAR(255),
    platforms    VARCHAR(255),
    translators  TEXT,
    steamid      VARCHAR(50),
    release_year INT,
    playtime     VARCHAR(100),
    format       VARCHAR(100),
    language     VARCHAR(100),
    rating       INT,
    tags         TEXT,
    status       VARCHAR(50)  NOT NULL DEFAULT 'Kütüphanede',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_oyunlar_kullanici (kullanici_id),
    -- A user cannot have two games with the same title (also enables import upsert/dedup).
    UNIQUE KEY uq_oyun_kullanici_title (kullanici_id, title),
    CONSTRAINT fk_oyunlar_kullanici
        FOREIGN KEY (kullanici_id) REFERENCES kullanicilar (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Activity logs
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS kullanici_loglari (
    id           INT       NOT NULL AUTO_INCREMENT,
    kullanici_id INT       NOT NULL,
    log_mesaji   VARCHAR(500) NOT NULL,
    log_tarihi   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_loglar_kullanici (kullanici_id),
    CONSTRAINT fk_loglar_kullanici
        FOREIGN KEY (kullanici_id) REFERENCES kullanicilar (id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- Seed an administrator account (optional).
-- Change the password before using in any real environment.
-- (Passwords are plain text until the BCrypt migration lands.)
-- ------------------------------------------------------------
-- INSERT INTO kullanicilar (kullanici_adi, sifre, is_admin)
-- VALUES ('admin', 'admin123', TRUE);

-- ------------------------------------------------------------
-- Upgrade an EXISTING database that predates the columns above.
-- Run these once if your `oyunlar` table was created before this version.
-- (Safe to skip on a fresh install created from the CREATE TABLE statements.)
-- ------------------------------------------------------------
-- ALTER TABLE oyunlar
--     ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
--     ADD UNIQUE KEY uq_oyun_kullanici_title (kullanici_id, title);
