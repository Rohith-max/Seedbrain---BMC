# NIDHI: The Digital Operating System for Indian Households

## 🏠 What is NIDHI?

NIDHI is the first document management and financial intelligence platform **built from the ground up for Indian families**. Not a global app with Indian features. Not a government website. A premium digital OS that understands Indian households.

### The Problem We Solve

- 📁 **Document Chaos**: Aadhaar, PAN, LIC, property docs scattered across family
- 📅 **Missed Deadlines**: Tax dates, insurance renewal, LIC premiums slip through
- 💰 **Missed Benefits**: Families don't know about ₹2-5L government schemes they qualify for
- 🗣️ **Language Barrier**: Can't access important services in mother tongue
- 👨‍👩‍👧‍👦 **Family Complexity**: Joint families, multi-generational homes, nominees

### Our Solution

- ✅ **Organize 30+ Indian document types** natively
- ✅ **AI identifies government schemes** your family qualifies for
- ✅ **Track LIC, EMI, taxes, investments** all in one place
- ✅ **Support for 8 Indian languages** with voice commands
- ✅ **Built for joint families** with multi-generational support
- ✅ **Bank-grade security** with local-first architecture

## 🚀 Key Features

### Indian Document Intelligence
Understands Aadhaar, PAN, Voter ID, Passport, LIC policies, Bank passbooks, Property deeds, School certificates, and 20+ more document types specific to Indian households.

### Government Scheme AI
Automatically identifies eligibility for:
- Pradhan Mantri Awas Yojana (PMAY)
- Ayushman Bharat - PMJAY
- Sukanya Samriddhi Yojana
- PM Kisan Samman Nidhi
- National Scholarships
- Senior Citizen Benefits
- And 30+ more schemes

### Financial Tracking (भारतीय)
Track LIC premiums, EMI payments, tax deadlines, SIP investments, FD maturity, school fees, insurance renewals - all with proactive reminders.

### Family Knowledge Graph
Multi-generational household support with nominee management, access control, and emergency protocols.

### 8 Indian Regional Languages
Hindi, Kannada, Tamil, Telugu, Malayalam, Marathi, Bengali - with voice commands and natural language responses.

## 📊 Demo Mode

Explore NIDHI with **realistic Indian household data**:

- **Sharma Family** (Bengaluru): IT professional household
- **Krishna Family** (Mysuru): Senior citizens with family
- **Srinivasan Family** (Chennai): Farmer family
- **Deshmukh Family** (Pune): Small business owner

Each demo shows actual scheme eligibility, financial tracking, and government benefits identification.

**Try it:** [http://localhost:3000/dashboard/demo](http://localhost:3000/dashboard/demo)

## 🛠️ Technical Stack

- **Framework**: Next.js 16 (React 19)
- **Styling**: Tailwind CSS 4 with custom NIDHI theme
- **Language**: TypeScript
- **State Management**: Zustand
- **Animations**: Framer Motion
- **Icons**: Lucide React
- **Database**: Drizzle ORM (Ready for PostgreSQL/MySQL)
- **Authentication**: Jose JWT
- **Charts**: Recharts

## 📦 Getting Started

### Prerequisites
- Node.js 18+
- npm or yarn

### Installation

```bash
# Clone the repository
git clone <repository>
cd nidhi

# Install dependencies
npm install

# Run development server
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

### Key Directories

```
src/
├── app/                    # Next.js app directory
│   ├── (auth)/            # Authentication routes
│   ├── (dashboard)/       # Dashboard routes
│   ├── api/               # API endpoints
│   └── page.tsx           # Landing page
├── components/
│   ├── landing/           # Landing page sections
│   ├── dashboard/         # Dashboard components
│   └── ui/                # Reusable UI components
├── lib/
│   ├── india-constants.ts # Indian documents, schemes, languages
│   ├── scheme-intelligence.ts # Government scheme AI engine
│   └── demo-data-india.ts # Realistic demo household data
└── styles/                # Global styles and themes
```

## 🎯 India-First Architecture

### Document Categories
- **Identity**: Aadhaar, PAN, Voter ID, Passport, DL
- **Education**: SSLC, PUC, Degree, Scholarships
- **Financial**: Bank Passbook, FD, LIC, Mutual Funds, ITR, Form 16
- **Property**: Sale Deed, Khata, EC, Property Tax
- **Healthcare**: Ayushman, Insurance, Medical Records
- **Vehicles**: RC Book, Insurance, Pollution Certificate
- **Government**: Pension, Ration Card, Caste Cert, E-Shram

### Government Schemes (50+)
AI engine with eligibility scoring for:
- Housing (PMAY)
- Healthcare (Ayushman Bharat)
- Savings (Sukanya Samriddhi)
- Agriculture (PM Kisan)
- Education (National Scholarships)
- Welfare (Senior Citizen, Disability)
- And state-specific schemes

### Family Relationships
Support for joint families: Father, Mother, Son, Daughter, Grandfather, Grandmother, Uncle, Aunt, Cousin, In-laws, Spouse, Dependents.

## 🔐 Security & Privacy

- **End-to-End Encryption**: AES-256
- **Local-First**: Data stays on device by default
- **Zero-Knowledge**: Server never sees raw documents
- **Encrypted Backups**: Only with user consent
- **Access Control**: Family member permissions
- **Compliance**: NIST standards, Indian privacy best practices

## 📖 Documentation

- **[NIDHI India-First Philosophy](./NIDHI-INDIA-FIRST.md)**: Complete India-first architecture
- **[Architecture Guide](./CLAUDE.md)**: Technical architecture
- **[Agent Setup](./AGENTS.md)**: AI agent configuration

## 🎓 Demo Family Profiles

### Sharma Family (Bengaluru)
- Income: ₹15L/year
- Members: 5 (joint family, 2 generations)
- Qualifies for: Sukanya Samriddhi, National Scholarship

### Krishna Family (Mysuru)
- Income: ₹6L/year
- Members: 5 (seniors + family)
- Qualifies for: Ayushman Bharat, Senior Benefits

### Srinivasan Family (Chennai)
- Income: ₹3L/year
- Members: 5 (farmer family)
- Qualifies for: PM Kisan, Sukanya Samriddhi, Scholarships

### Deshmukh Family (Pune)
- Income: ₹8L/year
- Members: 4 (business family)
- Qualifies for: PMAY, Sukanya Samriddhi

## 🚢 Deployment

### Build for Production
```bash
npm run build
npm run start
```

### Docker
```bash
docker build -t nidhi .
docker run -p 3000:3000 nidhi
```

## 🌟 Vision

When someone sees NIDHI, they should immediately think: **"This was built specifically for Indian families."** Not "This is a generic app with Indian branding."

NIDHI is the operating system Indian households have been waiting for.

## 📝 License

[Your License Here]

## 🤝 Contributing

We welcome contributions that enhance NIDHI's India-first mission.

---

**Built for India. By people who understand India.**
