#!/usr/bin/env bash

set -e
set -o pipefail

curl --location 'http://localhost:8080/fhir' \
--header 'Content-Type: application/json' \
--data '{
  "resourceType": "Bundle",
  "id": "DischargedonAntithromboticTherapyQICore4-bundle",
  "type": "transaction",
  "entry": [ {
    "resource": {
      "resourceType": "Encounter",
      "id": "denom-EXM104-2",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-encounter" ]
      },
      "status": "finished",
      "class": {
        "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
        "code": "IMP",
        "display": "inpatient encounter"
      },
      "type": [ {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "32485007",
          "display": "Hospital admission (procedure)"
        } ]
      } ],
      "subject": {
        "reference": "Patient/denom-EXM104"
      },
      "period": {
        "start": "2019-08-21T00:00:00-06:00",
        "end": "2019-12-19T08:15:00-07:00"
      },
      "diagnosis": [ {
        "condition": {
          "reference": "Condition/denom-EXM104-1"
        },
        "use": {
          "coding": [ {
            "system": "http://hl7.org/fhir/diagnosis-role",
            "code": "billing",
            "display": "Billing"
          } ]
        },
        "rank": 1
      } ]
    },
    "request": {
      "method": "PUT",
      "url": "Encounter/denom-EXM104-2"
    }
  }, {
    "resource": {
      "resourceType": "Condition",
      "id": "denom-EXM104-1",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-condition" ]
      },
      "verificationStatus": {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
          "code": "confirmed",
          "display": "Confirmed"
        } ]
      },
      "category": [ {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-category",
          "code": "encounter-diagnosis",
          "display": "Encounter Diagnosis"
        } ]
      } ],
      "code": {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "116288000",
          "display": "Paralytic stroke (disorder)"
        } ]
      },
      "subject": {
        "reference": "Patient/denom-EXM104"
      }
    },
    "request": {
      "method": "PUT",
      "url": "Condition/denom-EXM104-1"
    }
  }, {
    "resource": {
      "resourceType": "Condition",
      "id": "no-ip-EXM104-1",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-condition" ]
      },
      "verificationStatus": {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
          "code": "confirmed",
          "display": "Confirmed"
        } ]
      },
      "category": [ {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-category",
          "code": "encounter-diagnosis",
          "display": "Encounter Diagnosis"
        } ]
      } ],
      "code": {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "116288000",
          "display": "Paralytic stroke (disorder)"
        } ]
      },
      "subject": {
        "reference": "Patient/no-ip-EXM104"
      }
    },
    "request": {
      "method": "PUT",
      "url": "Condition/no-ip-EXM104-1"
    }
  }, {
    "resource": {
      "resourceType": "Encounter",
      "id": "denomexcl-EXM104-2",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-encounter" ]
      },
      "status": "finished",
      "class": {
        "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
        "code": "IMP",
        "display": "inpatient encounter"
      },
      "type": [ {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "32485007",
          "display": "Hospital admission (procedure)"
        } ]
      } ],
      "subject": {
        "reference": "Patient/denomexcl-EXM104"
      },
      "period": {
        "start": "2019-08-21T00:00:00-06:00",
        "end": "2019-12-19T08:15:00-07:00"
      },
      "diagnosis": [ {
        "condition": {
          "reference": "Condition/denomexcl-EXM104-1"
        },
        "use": {
          "coding": [ {
            "system": "http://hl7.org/fhir/diagnosis-role",
            "code": "billing",
            "display": "Billing"
          } ]
        },
        "rank": 1
      } ]
    },
    "request": {
      "method": "PUT",
      "url": "Encounter/denomexcl-EXM104-2"
    }
  }, {
    "resource": {
      "resourceType": "Encounter",
      "id": "no-ip-EXM104-2",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-encounter" ]
      },
      "status": "finished",
      "class": {
        "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
        "code": "IMP",
        "display": "inpatient encounter"
      },
      "type": [ {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "32485007",
          "display": "Hospital admission (procedure)"
        } ]
      } ],
      "subject": {
        "reference": "Patient/no-ip-EXM104"
      },
      "period": {
        "start": "2019-08-21T00:00:00-06:00",
        "end": "2019-12-19T08:15:00-07:00"
      },
      "diagnosis": [ {
        "condition": {
          "reference": "Condition/no-ip-EXM104-1"
        },
        "use": {
          "coding": [ {
            "system": "http://hl7.org/fhir/diagnosis-role",
            "code": "billing",
            "display": "Billing"
          } ]
        },
        "rank": 1
      } ]
    },
    "request": {
      "method": "PUT",
      "url": "Encounter/no-ip-EXM104-2"
    }
  }, {
    "resource": {
      "resourceType": "Condition",
      "id": "denomexcl-EXM104-1",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-condition" ]
      },
      "verificationStatus": {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
          "code": "confirmed",
          "display": "Confirmed"
        } ]
      },
      "category": [ {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-category",
          "code": "encounter-diagnosis",
          "display": "Encounter Diagnosis"
        } ]
      } ],
      "code": {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "116288000",
          "display": "Paralytic stroke (disorder)"
        } ]
      },
      "subject": {
        "reference": "Patient/denomexcl-EXM104"
      }
    },
    "request": {
      "method": "PUT",
      "url": "Condition/denomexcl-EXM104-1"
    }
  }, {
    "resource": {
      "resourceType": "Patient",
      "id": "numer-EXM104",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient" ]
      },
      "extension": [ {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2106-3",
            "display": "White"
          }
        } ]
      }, {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2186-5",
            "display": "Not Hispanic or Latino"
          }
        } ]
      } ],
      "identifier": [ {
        "use": "usual",
        "type": {
          "coding": [ {
            "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
            "code": "MR",
            "display": "Medical Record Number"
          } ]
        },
        "system": "http://hospital.smarthealthit.org",
        "value": "9999999911"
      } ],
      "name": [ {
        "family": "Jones",
        "given": [ "Louise" ]
      } ],
      "gender": "female",
      "birthDate": "1971-11-21"
    },
    "request": {
      "method": "PUT",
      "url": "Patient/numer-EXM104"
    }
  }, {
    "resource": {
      "resourceType": "MedicationRequest",
      "id": "no-ip-EXM104-5",
      "status": "completed",
      "intent": "order",
      "category": [ {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/medicationrequest-category",
          "code": "discharge",
          "display": "Discharge"
        } ]
      } ],
      "medicationCodeableConcept": {
        "coding": [ {
          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
          "code": "1037045",
          "display": "dabigatran etexilate 150 MG Oral Capsule"
        } ]
      },
      "subject": {
        "reference": "Patient/no-ip-EXM104"
      },
      "authoredOn": "2019-12-17T08:00:00"
    },
    "request": {
      "method": "PUT",
      "url": "MedicationRequest/no-ip-EXM104-5"
    }
  },
  {
    "resource": {
      "resourceType": "Patient",
      "id": "denom-EXM104",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient" ]
      },
      "extension": [ {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2054-5",
            "display": "Black or African American"
          }
        } ]
      }, {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2135-2",
            "display": "Hispanic or Latino"
          }
        } ]
      } ],
      "identifier": [ {
        "use": "usual",
        "type": {
          "coding": [ {
            "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
            "code": "MR",
            "display": "Medical Record Number"
          } ]
        },
        "system": "http://hospital.smarthealthit.org",
        "value": "9999999910"
      } ],
      "name": [ {
        "family": "Jones",
        "given": [ "Rick" ]
      } ],
      "gender": "male",
      "birthDate": "1955-11-05"
    },
    "request": {
      "method": "PUT",
      "url": "Patient/denom-EXM104"
    }
  }, 
  {
    "resource": {
      "resourceType": "MedicationRequest",
      "id": "numer-EXM104-5",
      "status": "completed",
      "intent": "order",
      "category": [ {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/medicationrequest-category",
          "code": "discharge",
          "display": "Discharge"
        } ]
      } ],
      "medicationCodeableConcept": {
        "coding": [ {
          "system": "http://www.nlm.nih.gov/research/umls/rxnorm",
          "code": "1037045",
          "display": "dabigatran etexilate 150 MG Oral Capsule"
        } ]
      },
      "subject": {
        "reference": "Patient/numer-EXM104"
      },
      "authoredOn": "2021-12-17T08:00:00"
    },
    "request": {
      "method": "PUT",
      "url": "MedicationRequest/numer-EXM104-5"
    }
  }, 
  {
    "resource": {
      "resourceType": "Condition",
      "id": "numer-EXM104-1",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-condition" ]
      },
      "verificationStatus": {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-ver-status",
          "code": "confirmed",
          "display": "Confirmed"
        } ]
      },
      "category": [ {
        "coding": [ {
          "system": "http://terminology.hl7.org/CodeSystem/condition-category",
          "code": "encounter-diagnosis",
          "display": "Encounter Diagnosis"
        } ]
      } ],
      "code": {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "116288000",
          "display": "Paralytic stroke (disorder)"
        } ]
      },
      "subject": {
        "reference": "Patient/numer-EXM104"
      }
    },
    "request": {
      "method": "PUT",
      "url": "Condition/numer-EXM104-1"
    }
  }, 
  {
    "resource": {
      "resourceType": "Encounter",
      "id": "numer-EXM104-2",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-encounter" ]
      },
      "status": "finished",
      "class": {
        "system": "http://terminology.hl7.org/CodeSystem/v3-ActCode",
        "code": "IMP",
        "display": "inpatient encounter"
      },
      "type": [ {
        "coding": [ {
          "system": "http://snomed.info/sct",
          "code": "32485007",
          "display": "Hospital admission (procedure)"
        } ]
      } ],
      "subject": {
        "reference": "Patient/numer-EXM104"
      },
      "period": {
        "start": "2021-10-21T00:00:00-06:00",
        "end": "2022-01-19T08:15:00-07:00"
      },
      "diagnosis": [ {
        "condition": {
          "reference": "Condition/numer-EXM104-1"
        },
        "use": {
          "coding": [ {
            "system": "http://hl7.org/fhir/diagnosis-role",
            "code": "billing",
            "display": "Billing"
          } ]
        },
        "rank": 1
      } ]
    },
    "request": {
      "method": "PUT",
      "url": "Encounter/numer-EXM104-2"
    }
  }, 
  {
    "resource": {
      "resourceType": "Patient",
      "id": "no-ip-EXM104",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient" ]
      },
      "extension": [ {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2106-3",
            "display": "White"
          }
        } ]
      }, {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2186-5",
            "display": "Not Hispanic or Latino"
          }
        } ]
      } ],
      "identifier": [ {
        "use": "usual",
        "type": {
          "coding": [ {
            "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
            "code": "MR",
            "display": "Medical Record Number"
          } ]
        },
        "system": "http://hospital.smarthealthit.org",
        "value": "9999999911"
      } ],
      "name": [ {
        "family": "Jones",
        "given": [ "Louise" ]
      } ],
      "gender": "female",
      "birthDate": "2015-11-21"
    },
    "request": {
      "method": "PUT",
      "url": "Patient/no-ip-EXM104"
    }
  },
  {
    "resource": {
      "resourceType": "Patient",
      "id": "denomexcl-EXM104",
      "meta": {
        "profile": [ "http://hl7.org/fhir/us/core/StructureDefinition/us-core-patient" ]
      },
      "extension": [ {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2054-5",
            "display": "Black or African American"
          }
        } ]
      }, {
        "url": "http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity",
        "extension": [ {
          "url": "ombCategory",
          "valueCoding": {
            "system": "urn:oid:2.16.840.1.113883.6.238",
            "code": "2135-2",
            "display": "Hispanic or Latino"
          }
        } ]
      } ],
      "identifier": [ {
        "use": "usual",
        "type": {
          "coding": [ {
            "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
            "code": "MR",
            "display": "Medical Record Number"
          } ]
        },
        "system": "http://hospital.smarthealthit.org",
        "value": "9999999910"
      } ],
      "name": [ {
        "family": "Jones",
        "given": [ "Rick" ]
      } ],
      "gender": "male",
      "birthDate": "1955-11-05"
    },
    "request": {
      "method": "PUT",
      "url": "Patient/denomexcl-EXM104"
    }
  } ]
}'
