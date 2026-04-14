<?php
include_once '../classes/Etudiant.php';
include_once '../connexion/Connexion.php';
include_once '../dao/IDao.php';

class EtudiantService implements IDao {
    private $connexion;

    function __construct() { $this->connexion = new Connexion(); }

    public function create($o) {
        $query = "INSERT INTO Etudiant (nom, prenom, ville, sexe)
                  VALUES (:nom, :prenom, :ville, :sexe)";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->execute([
            ':nom' => $o->getNom(),
            ':prenom' => $o->getPrenom(),
            ':ville' => $o->getVille(),
            ':sexe' => $o->getSexe()
        ]);
    }

    public function findAllApi() {
        $req = $this->connexion->getConnexion()->query("SELECT * FROM Etudiant");
        return $req->fetchAll(PDO::FETCH_ASSOC);
    }

    public function delete($o) {
        $query = "DELETE FROM Etudiant WHERE id = :id";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->execute([':id' => $o->getId()]);
    }

    public function update($o) {
        $query = "UPDATE Etudiant SET nom = :nom, prenom = :prenom, ville = :ville, sexe = :sexe WHERE id = :id";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->execute([
            ':nom' => $o->getNom(),
            ':prenom' => $o->getPrenom(),
            ':ville' => $o->getVille(),
            ':sexe' => $o->getSexe(),
            ':id' => $o->getId()
        ]);
    }

    public function findAll() {
        $req = $this->connexion->getConnexion()->query("SELECT * FROM Etudiant");
        $etudiants = [];
        while ($row = $req->fetch(PDO::FETCH_ASSOC)) {
            $etudiants[] = new Etudiant($row['id'], $row['nom'], $row['prenom'], $row['ville'], $row['sexe']);
        }
        return $etudiants;
    }

    public function findById($id) {
        $query = "SELECT * FROM Etudiant WHERE id = :id";
        $stmt = $this->connexion->getConnexion()->prepare($query);
        $stmt->execute([':id' => $id]);
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        if ($row) {
            return new Etudiant($row['id'], $row['nom'], $row['prenom'], $row['ville'], $row['sexe']);
        }
        return null;
    }
}
?>