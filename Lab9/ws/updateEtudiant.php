<?php
include_once '../service/EtudiantService.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $id = $_POST['id'] ?? null;
    $nom = $_POST['nom'] ?? '';
    $prenom = $_POST['prenom'] ?? '';
    $ville = $_POST['ville'] ?? '';
    $sexe = $_POST['sexe'] ?? '';

    if ($id) {
        $es = new EtudiantService();
        // Create an Etudiant object with the received data
        $e = new Etudiant($id, $nom, $prenom, $ville, $sexe);
        $es->update($e);

        header('Content-Type: application/json');
        echo json_encode(["status" => "success", "message" => "Etudiant mis à jour"]);
    } else {
        header('Content-Type: application/json');
        echo json_encode(["status" => "error", "message" => "Données incomplètes"]);
    }
}
?>