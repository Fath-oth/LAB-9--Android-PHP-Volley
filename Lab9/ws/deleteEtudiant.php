<?php
include_once '../service/EtudiantService.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Get the ID from the POST request
    $id = $_POST['id'] ?? null;

    if ($id) {
        $es = new EtudiantService();
        // The delete method in EtudiantService expects an Etudiant object
        // We create a dummy object with just the ID
        $e = new Etudiant($id, "", "", "", "");
        $es->delete($e);

        header('Content-Type: application/json');
        echo json_encode(["status" => "success", "message" => "Etudiant supprimé"]);
    } else {
        header('Content-Type: application/json');
        echo json_encode(["status" => "error", "message" => "ID manquant"]);
    }
}
?>