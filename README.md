# PoC Chat — Your Car Your Way

Proof‑of‑concept d’un chat de support, front Vue 3 + Vite et back Spring Boot (Java 17) utilisant MongoDB Atlas.

---

##  Pré-requis

- **Java 17+** ([Adoptium](https://adoptium.net/fr/) conseillé)
- **Maven 3.6+**
- **Node.js 18+** ([Télécharger](https://nodejs.org/))
- Un compte **[MongoDB Atlas](https://cloud.mongodb.com)** (gratuit possible)

---

##  Configuration MongoDB

1. Créez un cluster gratuit sur [MongoDB Atlas](https://www.mongodb.com/cloud/atlas).
2. Dans Atlas → **Database → Connect → Connect your application**, copiez la string de connexion.
3. Dans votre terminal, définissez la variable d’environnement `SPRING_DATA_MONGODB_URI` avec votre URI :
   ```bash
   # macOS / Linux
   export SPRING_DATA_MONGODB_URI="<VOTRE_URI_ATLAS>?retryWrites=true&w=majority"
   
   # Windows (PowerShell)
   setx SPRING_DATA_MONGODB_URI "<VOTRE_URI_ATLAS>?retryWrites=true&w=majority"
4. Vérifiez
   ```bash
   echo $SPRING_DATA_MONGODB_URI

# Prérequis backend (Spring Boot)

- Java 17 (obligatoire)
- Maven 3.8+
- Un compte [MongoDB Atlas](https://www.mongodb.com/cloud/atlas) (base de données MongoDB hébergée)
- Une variable d'environnement nommée `SPRING_DATA_MONGODB_URI` contenant la chaîne de connexion MongoDB

  Pour lancer l'api : mvn spring-boot:run

## ⚙️ Prérequis frontend (Vue 3 + Vite)

- Node.js **v22.12.0**
- npm **v10.9.2**

---

### 📦 Installation des dépendances

Placez-vous dans le dossier `chat-frontend` puis exécutez :

```bash
npm install

Pour lancer l'application : npm run dev
