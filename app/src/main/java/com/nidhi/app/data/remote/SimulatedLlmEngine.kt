package com.nidhi.app.data.remote

import kotlinx.coroutines.delay

/**
 * Offline-capable simulated LLM engine.
 * Returns contextually relevant responses about Indian government schemes,
 * documents, and family finance — no internet or API key needed.
 *
 * To switch to a real LLM, set USE_SIMULATED_LLM = false in build.gradle.kts
 * and provide a valid LLM_API_KEY.
 */
class SimulatedLlmEngine {

    /** Returns a simulated chat reply with a realistic ~600ms delay. */
    suspend fun chat(userMessage: String): String {
        delay(650) // simulate network latency
        return buildResponse(userMessage.lowercase().trim())
    }

    /** Returns a simulated document summary for the given OCR text. */
    suspend fun summarise(ocrText: String, docType: String): String {
        delay(900)
        return buildDocumentSummary(ocrText, docType)
    }

    // ── Response builder ─────────────────────────────────────────────────────

    private fun buildResponse(q: String): String = when {

        // ── Benefits ──────────────────────────────────────────────────────────
        q.containsAny("pmay", "pradhan mantri awas", "housing scheme", "home loan subsidy") ->
            """**Pradhan Mantri Awas Yojana (PMAY)**

PMAY provides housing for all. Key details:

• **PMAY-Urban**: Credit-linked subsidy up to ₹2.67 lakh for EWS/LIG categories
• **PMAY-Gramin**: ₹1.2 lakh (plains) or ₹1.3 lakh (hilly areas) for rural families
• **Eligibility**: Families without pucca house, income below ₹18 lakh/year
• **Required docs**: Aadhaar, income certificate, bank account, land documents

👉 Apply at: pmayg.nic.in (rural) or pmaymis.gov.in (urban)

Would you like help checking your eligibility or uploading documents?"""

        q.containsAny("pmjay", "ayushman", "health insurance", "health cover", "medical scheme") ->
            """**Ayushman Bharat – PM Jan Arogya Yojana (PMJAY)**

India's largest health insurance scheme:

• **Cover**: ₹5 lakh per family per year
• **Scope**: 1,949 medical procedures across 25+ specialties
• **Hospitals**: 25,000+ empanelled government and private hospitals
• **Eligibility**: SECC 2011 database listed families; no premium to pay
• **Required docs**: Aadhaar card, ration card

📱 Check eligibility: mera.pmjay.gov.in or call 14555

Would you like me to check which documents you have uploaded for this scheme?"""

        q.containsAny("pm kisan", "pmkisan", "farmer", "agriculture", "kisan scheme") ->
            """**PM-KISAN Samman Nidhi**

Direct income support for farmers:

• **Amount**: ₹6,000 per year (₹2,000 every 4 months) directly to bank
• **Eligibility**: Land-owning farmer families; excludes institutional land holders
• **Required docs**: Aadhaar, land records (Khasra/Khatauni), bank passbook
• **Exclusions**: Government employees, income-tax payers, professionals

✅ Register at: pmkisan.gov.in
📞 Helpline: 155261 / 011-24300606

Shall I help you track your PM-KISAN payment status?"""

        q.containsAny("sukanya", "girl child", "daughter scheme") ->
            """**Sukanya Samriddhi Yojana (SSY)**

A savings scheme for the girl child:

• **Interest rate**: 8.2% per annum (one of the highest guaranteed returns)
• **Tax benefit**: Deposits up to ₹1.5 lakh exempt under Section 80C; maturity amount is tax-free
• **Tenure**: 21 years from account opening, or until marriage after age 18
• **Min deposit**: ₹250/year; Max: ₹1.5 lakh/year
• **Eligibility**: Girl child below 10 years; max 2 accounts per family
• **Required docs**: Girl's birth certificate, parent's Aadhaar

🏦 Open at any post office or authorised bank branch"""

        q.containsAny("mudra", "business loan", "startup loan", "msme") ->
            """**PM MUDRA Yojana**

Loans for micro and small businesses (no collateral):

| Category | Loan Amount |
|----------|-------------|
| Shishu   | Up to ₹50,000 |
| Kishore  | ₹50,001 – ₹5 lakh |
| Tarun    | ₹5 lakh – ₹10 lakh |

• **Eligibility**: Non-farm small/micro enterprises, individual entrepreneurs
• **Required docs**: Aadhaar, PAN, business proof, bank statement (6 months)
• **Apply**: Any bank, MFI, or mudra.org.in

No processing fee for Shishu loans!"""

        q.containsAny("atal pension", "apy", "pension", "retirement") ->
            """**Atal Pension Yojana (APY)**

Guaranteed pension for unorganised sector workers:

• **Pension**: ₹1,000 – ₹5,000/month after age 60
• **Age**: 18–40 years to enrol
• **Government co-contribution**: 50% of contribution or ₹1,000/year (for eligible)
• **Required docs**: Aadhaar, savings bank account, mobile number

💡 The younger you join, the lower the monthly premium!

Example: Join at 18 → ₹42/month for ₹1,000 pension
Join at 30 → ₹116/month for ₹1,000 pension"""

        q.containsAny("pmsby", "accident insurance", "insurance", "jeevan jyoti") ->
            """**Two Key Insurance Schemes**

**PMSBY (Accident Insurance)**
• Cover: ₹2 lakh for accidental death/disability
• Premium: Just ₹20/year (auto-debited from bank)
• Age: 18–70 years

**PMJJBY (Life Insurance)**
• Cover: ₹2 lakh for death (any cause)
• Premium: ₹436/year
• Age: 18–50 years

Both available through your bank account. Combined protection for ₹456/year — that's less than ₹1.25/day! ✅"""

        // ── Documents ─────────────────────────────────────────────────────────
        q.containsAny("aadhaar", "aadhar", "uid", "uidai") ->
            """**Aadhaar – Key Information**

• **Valid for**: Lifetime (no expiry for standard Aadhaar)
• **Update**: Name, address, DOB can be updated at Aadhaar centres or online (uidai.gov.in)
• **Download e-Aadhaar**: uidai.gov.in → My Aadhaar → Download Aadhaar
• **PVC card**: Order online at uidai.gov.in for ₹50

⚠️ Never share your full 12-digit number publicly. Use **Masked Aadhaar** (shows only last 4 digits) for most purposes.

📱 mAadhaar app lets you carry digital Aadhaar on phone.

Is your family's Aadhaar uploaded in NIDHI?"""

        q.containsAny("pan card", "pan number", "income tax") ->
            """**PAN Card – Key Information**

• **Valid for**: Lifetime (no expiry)
• **Required for**: Income tax filing, bank accounts, investments over ₹50,000, property transactions
• **Apply/update**: incometax.gov.in or NSDL/UTIISL portal
• **Instant PAN**: Get e-PAN free using Aadhaar at incometax.gov.in

💡 **PAN-Aadhaar Linking**: Link PAN with Aadhaar to avoid ₹1,000 penalty and PAN deactivation.
Deadline: Link at incometax.gov.in → Quick Links → Link Aadhaar"""

        q.containsAny("passport", "travel document", "visa") ->
            """**Passport – Renewal & Application**

• **Validity**: 10 years (adult), 5 years (minor below 18)
• **Apply/Renew**: passportindia.gov.in → Apply for Fresh/Renewal
• **Tatkaal scheme**: Expedited processing in 1-3 working days (extra fee)
• **Required docs**: Aadhaar, birth certificate, address proof

⏰ **Renewal tip**: Apply at least 6 months before expiry for international travel.

Your passport in NIDHI shows it expires soon. Want me to set a renewal reminder?"""

        q.containsAny("driving licence", "driving license", "dl ") ->
            """**Driving Licence – Renewal & Update**

• **Validity**: 20 years (LMV), or until age 50, whichever is earlier; renew within 30 days of expiry
• **Renew online**: parivahan.gov.in → Driving Licence Services
• **Required docs**: Current DL, Aadhaar/address proof, Form 9

⚠️ Driving with expired licence attracts fine of ₹5,000 under Motor Vehicles Act 2019.

Shall I add a renewal reminder to your calendar?"""

        // ── Tax ───────────────────────────────────────────────────────────────
        q.containsAny("itr", "income tax return", "tax filing", "80c", "tax saving") ->
            """**Income Tax Filing – Key Dates & Tips**

📅 **Important dates**:
• ITR filing deadline: **31 July** (for salaried, no audit)
• Belated return: Up to **31 December** (with penalty)

💰 **Popular deductions**:
| Section | Deduction | Limit |
|---------|-----------|-------|
| 80C | PPF, ELSS, LIC, NSC, tuition fees | ₹1.5 lakh |
| 80D | Health insurance premium | ₹25,000–₹75,000 |
| 80G | Charitable donations | 50–100% |
| 24(b) | Home loan interest | ₹2 lakh |

📱 File free at: incometax.gov.in"""

        // ── General Finance ───────────────────────────────────────────────────
        q.containsAny("epf", "provident fund", "pf balance") ->
            """**EPF – Employee Provident Fund**

• **Contribution**: 12% of basic salary by employee + 12% by employer
• **Interest rate**: 8.25% p.a. (2023-24)
• **Check balance**: EPFO member portal (epfindia.gov.in) or UMANG app
• **UAN activation**: Link Aadhaar with UAN for seamless withdrawals

📱 **UMANG app** → EPFO → Member Passbook → check real-time balance

💡 You can transfer PF online when switching jobs — no need to visit office."""

        q.containsAny("ration card", "food security", "pds", "subsidised food") ->
            """**Ration Card – National Food Security**

**Types**:
• **AAY (Antyodaya)**: Poorest families — 35 kg grain/month at ₹2/kg (wheat) or ₹3/kg (rice)
• **PHH (Priority Household)**: 5 kg grain/month per person at subsidised rates

**Apply/Update**: Your state's Food & Civil Supplies department (online or offline)
**Required docs**: Aadhaar (all family members), address proof, income proof

🌾 Under PMGKAY, free grains provided to National Food Security Act beneficiaries."""

        // ── NIDHI app features ────────────────────────────────────────────────
        q.containsAny("how to scan", "scan document", "upload document", "add document") ->
            """**How to Scan Documents in NIDHI**

1. Tap **Documents** in the bottom navigation
2. Tap the **Scan** button (camera icon, bottom right)
3. Select document type (Aadhaar, PAN, Passport, etc.)
4. Give it a name (e.g. "Dad's Aadhaar")
5. Point camera at document and tap the **capture button**
6. NIDHI will automatically:
   - Extract text using OCR
   - Generate an AI summary
   - Detect expiry dates
   - Save securely offline

💡 Hold the camera steady and ensure good lighting for best OCR accuracy."""

        q.containsAny("emergency", "sos", "help", "accident") ->
            """**NIDHI Emergency Features**

🚨 **SOS Screen** (tap the red emergency icon in Home):
• 5-second countdown before sending alert
• Automatically dials 112 (national emergency)
• Quick access to 108 (ambulance) and 100 (police)

**Indian Emergency Numbers**:
| Service | Number |
|---------|--------|
| National Emergency | **112** |
| Police | **100** |
| Fire | **101** |
| Ambulance | **108** |
| Women Helpline | **1091** |
| Child Helpline | **1098** |
| Senior Citizen | **14567** |

Stay safe! 🙏"""

        q.containsAny("family member", "add family", "member") ->
            """**Managing Family Members in NIDHI**

Go to **Settings → Family Members** to:
• Add family members (name, relation, date of birth)
• Link documents to specific members
• Track benefits and documents per person

**Supported relations**: Self, Spouse, Son, Daughter, Father, Mother, Brother, Sister, Grandparent

💡 Adding family members helps NIDHI calculate a more accurate Family Health Score and find schemes your family qualifies for."""

        // ── Greetings ─────────────────────────────────────────────────────────
        q.containsAny("hello", "hi ", "namaste", "namaskar", "hey", "good morning",
            "good afternoon", "good evening") ->
            """**Namaste! 🙏 I'm NIDHI AI**

I'm your family's financial and document assistant. I can help you with:

📂 **Documents** — Aadhaar, PAN, Passport, Driving Licence tips
🏛️ **Government Schemes** — PMAY, PMJAY, PM-KISAN, Mudra, APY and more
💰 **Financial Planning** — Tax filing, EPF, savings schemes
🔔 **Deadlines** — Renewal dates and benefit application windows

What would you like to know today?"""

        q.containsAny("thank", "thanks", "shukriya", "dhanyavad") ->
            """You're welcome! 🙏

If you need any help with:
• Finding eligible government schemes
• Document renewal reminders
• Financial planning tips

Just ask — I'm always here. 

**Pro tip**: Check your Family Health Score on the Home screen to see what needs attention next!"""

        q.containsAny("what can you do", "help me", "features", "capabilities") ->
            """**NIDHI AI can help you with:**

🏛️ **Government Benefits**
→ PMAY, PMJAY, PM-KISAN, Sukanya Samriddhi, MUDRA, APY, PMSBY and 50+ more schemes

📂 **Document Management**
→ Aadhaar, PAN, Passport, Driving Licence, Insurance, Land Records

💰 **Financial Planning**
→ Tax saving (80C, 80D), EPF, PPF, NPS, Fixed Deposits

📅 **Deadlines & Reminders**
→ Document expiry, ITR filing, premium payments

🚨 **Emergency**
→ Quick access to emergency numbers and contacts

Ask me anything — in English or Hindi!"""

        // ── Default ───────────────────────────────────────────────────────────
        else ->
            """I can help with that! Here are some things you might want to know:

• **Government schemes** — Try asking about PMAY, PMJAY, PM-KISAN, Mudra loans, or APY
• **Documents** — Aadhaar, PAN, Passport renewal and update tips
• **Tax filing** — ITR deadlines, 80C deductions, PAN-Aadhaar linking
• **Emergency** — Indian helpline numbers and SOS features

Could you rephrase your question or pick one of the topics above? I'm here to help! 🙏"""
    }

    private fun buildDocumentSummary(ocrText: String, docType: String): String {
        val typeUpper = docType.uppercase()
        return when {
            typeUpper.contains("AADHAAR") || typeUpper.contains("AADHAR") ->
                """{"headline":"Aadhaar Identity Card","keyPoints":["Government-issued unique identity document","Issued by UIDAI","Valid for lifetime — no expiry","Contains biometric and demographic data"],"expiryDateStr":null,"extractedIds":{"Aadhaar":"XXXX-XXXX-${(1000..9999).random()}"}}"""

            typeUpper.contains("PAN") ->
                """{"headline":"Permanent Account Number Card","keyPoints":["Income tax identity document","Issued by Income Tax Department","Valid for lifetime","Required for financial transactions above ₹50,000"],"expiryDateStr":null,"extractedIds":{"PAN":"${randomPan()}"}}"""

            typeUpper.contains("PASSPORT") ->
                """{"headline":"Indian Passport","keyPoints":["International travel document","Issued by Ministry of External Affairs","Valid for 10 years (adult) or 5 years (minor)","Renewal required before expiry for visa applications"],"expiryDateStr":"${futureDate(730)}","extractedIds":{"Passport Number":"${randomPassportNo()}"}}"""

            typeUpper.contains("DRIVING") || typeUpper.contains("LICENCE") || typeUpper.contains("LICENSE") ->
                """{"headline":"Driving Licence","keyPoints":["Vehicle operation permit","Issued by Regional Transport Office","Valid for 20 years or until age 50","Renew within 30 days of expiry to avoid penalty"],"expiryDateStr":"${futureDate(1825)}","extractedIds":{"DL Number":"${randomDlNo()}"}}"""

            typeUpper.contains("INSURANCE") ->
                """{"headline":"Insurance Policy Document","keyPoints":["Financial protection document","Contains policy number and coverage details","Review premium due dates carefully","Keep nomination details updated"],"expiryDateStr":"${futureDate(365)}","extractedIds":{"Policy Number":"POL${(100000..999999).random()}"}}"""

            typeUpper.contains("RATION") ->
                """{"headline":"Ration Card","keyPoints":["Food security document","Issued by State Food Department","Entitles to subsidised food grains","Update when family composition changes"],"expiryDateStr":null,"extractedIds":{"Ration Card No":"RC${(10000000..99999999).random()}"}}"""

            typeUpper.contains("BIRTH") ->
                """{"headline":"Birth Certificate","keyPoints":["Proof of date and place of birth","Issued by Municipal Corporation/Gram Panchayat","Required for school admissions, passport, marriage registration","Keep original and attested copies safe"],"expiryDateStr":null,"extractedIds":{}}"""

            typeUpper.contains("VOTER") ->
                """{"headline":"Voter ID Card (EPIC)","keyPoints":["Electoral photo identity card","Issued by Election Commission of India","Valid for lifetime","Update address online at voters.eci.gov.in"],"expiryDateStr":null,"extractedIds":{"EPIC Number":"${randomEpic()}"}}"""

            else ->
                """{"headline":"Document: $docType","keyPoints":["Document successfully scanned and stored","Text extracted via OCR","Review extracted content for accuracy","Set expiry reminder if applicable"],"expiryDateStr":null,"extractedIds":{}}"""
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun String.containsAny(vararg keywords: String) =
        keywords.any { this.contains(it) }

    private fun randomPan(): String {
        val letters = ('A'..'Z').toList()
        return "${letters.random()}${letters.random()}${letters.random()}${letters.random()}${letters.random()}" +
            "${(1000..9999).random()}${letters.random()}"
    }

    private fun randomPassportNo(): String {
        val letters = ('A'..'Z').toList()
        return "${letters.random()}${(1000000..9999999).random()}"
    }

    private fun randomDlNo(): String {
        val states = listOf("MH", "DL", "KA", "TN", "GJ", "UP", "RJ", "MP")
        return "${states.random()}${(10..99).random()}${(10000000000..99999999999).random()}"
    }

    private fun randomEpic(): String {
        val letters = ('A'..'Z').toList()
        return "${letters.random()}${letters.random()}${letters.random()}${(1000000..9999999).random()}"
    }

    private fun futureDate(daysFromNow: Int): String {
        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, daysFromNow)
        return java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(cal.time)
    }
}
