-- Création de l'administrateur initial
-- Le mot de passe 'admin123' est hashé avec BCrypt
INSERT INTO utilisateurs (email, password, role)
VALUES (
           'admin@test.com',  -- Email facilement reconnaissable comme admin
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- Hashage BCrypt de 'admin123'
           'ADMINISTRATEUR'  -- Rôle administrateur tel que défini dans l'enum Role
       ) ON DUPLICATE KEY UPDATE email=email;  -- Évite les doublons si réexécuté

-- Création d'un utilisateur entreprise pour les tests
INSERT INTO utilisateurs (email, password, role)
VALUES (
           'user@test.com',  -- Email standard pour un utilisateur test
           '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- Même mot de passe pour simplifier les tests
           'ENTREPRISE'  -- Rôle entreprise pour tester les permissions différentes
       ) ON DUPLICATE KEY UPDATE email=email;