package com.healthcare.claims.api.tenants.seed;

import com.healthcare.claims.api.tenants.model.Tenant;
import com.healthcare.claims.api.tenants.model.TenantStatus;
import com.healthcare.claims.api.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantSeeder implements CommandLineRunner {

    private final TenantRepository tenantRepository;

    @Override
    public void run(String... args) {
        if (tenantRepository.count() > 0) {
            log.info("Tenants already seeded, skipping. Count: {}", tenantRepository.count());
            return;
        }

        log.info("Seeding 100 tenants...");
        List<Tenant> tenants = buildTenants();
        tenantRepository.saveAll(tenants);
        log.info("Successfully seeded {} tenants", tenants.size());
    }

    private List<Tenant> buildTenants() {
        String[][] tenantData = {
            {"TNT-001", "Horizon Health Partners", "horizon-health.com", "enterprise", "admin@horizon-health.com", "(555) 100-0001", "100 Horizon Blvd, Boston, MA 02101", "200"},
            {"TNT-002", "Pacific Wellness Group", "pacificwellness.com", "enterprise", "admin@pacificwellness.com", "(555) 100-0002", "200 Pacific Ave, San Francisco, CA 94102", "150"},
            {"TNT-003", "Summit Care Alliance", "summitcare.com", "enterprise", "admin@summitcare.com", "(555) 100-0003", "300 Summit Dr, Denver, CO 80201", "175"},
            {"TNT-004", "Evergreen Medical Network", "evergreenmedical.com", "enterprise", "admin@evergreenmedical.com", "(555) 100-0004", "400 Evergreen Way, Seattle, WA 98101", "120"},
            {"TNT-005", "Pinnacle Health Systems", "pinnaclehealth.com", "enterprise", "admin@pinnaclehealth.com", "(555) 100-0005", "500 Pinnacle Ct, Chicago, IL 60601", "300"},
            {"TNT-006", "Lakewood Community Hospital", "lakewoodhosp.com", "professional", "admin@lakewoodhosp.com", "(555) 100-0006", "601 Lakewood Rd, Cleveland, OH 44101", "80"},
            {"TNT-007", "Coastal Family Medicine", "coastalfamily.com", "professional", "admin@coastalfamily.com", "(555) 100-0007", "702 Coastal Hwy, Miami, FL 33101", "45"},
            {"TNT-008", "Redwood Clinical Associates", "redwoodclinical.com", "professional", "admin@redwoodclinical.com", "(555) 100-0008", "803 Redwood Ln, Sacramento, CA 95814", "60"},
            {"TNT-009", "Northern Star Health Plan", "northernstarhealth.com", "enterprise", "admin@northernstarhealth.com", "(555) 100-0009", "904 Northern Star Blvd, Minneapolis, MN 55401", "250"},
            {"TNT-010", "Sunrise Diagnostics Lab", "sunrisediag.com", "professional", "admin@sunrisediag.com", "(555) 100-0010", "1005 Sunrise Pkwy, Phoenix, AZ 85001", "35"},
            {"TNT-011", "Meadowbrook Pediatrics", "meadowbrookpeds.com", "starter", "admin@meadowbrookpeds.com", "(555) 100-0011", "1106 Meadowbrook Dr, Austin, TX 73301", "20"},
            {"TNT-012", "Silver Ridge Rehabilitation", "silverridgerehab.com", "professional", "admin@silverridgerehab.com", "(555) 100-0012", "1207 Silver Ridge Ave, Nashville, TN 37201", "55"},
            {"TNT-013", "Cascade Behavioral Health", "cascadebehavioral.com", "professional", "admin@cascadebehavioral.com", "(555) 100-0013", "1308 Cascade St, Portland, OR 97201", "40"},
            {"TNT-014", "Beacon Insurance Partners", "beaconinsurance.com", "enterprise", "admin@beaconinsurance.com", "(555) 100-0014", "1409 Beacon Ave, Hartford, CT 06101", "500"},
            {"TNT-015", "Harmony Wellness Center", "harmonywellness.com", "starter", "admin@harmonywellness.com", "(555) 100-0015", "1510 Harmony Ln, Boulder, CO 80301", "15"},
            {"TNT-016", "Atlas Primary Care", "atlasprimarycare.com", "professional", "admin@atlasprimarycare.com", "(555) 100-0016", "1611 Atlas Rd, Atlanta, GA 30301", "70"},
            {"TNT-017", "Trident Surgical Group", "tridentsurgical.com", "professional", "admin@tridentsurgical.com", "(555) 100-0017", "1712 Trident Way, Houston, TX 77001", "50"},
            {"TNT-018", "Willow Creek Health Services", "willowcreekhs.com", "enterprise", "admin@willowcreekhs.com", "(555) 100-0018", "1813 Willow Creek Rd, Charlotte, NC 28201", "130"},
            {"TNT-019", "Cornerstone Medical Group", "cornerstonemedical.com", "professional", "admin@cornerstonemedical.com", "(555) 100-0019", "1914 Cornerstone Blvd, Philadelphia, PA 19101", "85"},
            {"TNT-020", "Sapphire Telehealth Solutions", "sapphiretelehealth.com", "enterprise", "admin@sapphiretelehealth.com", "(555) 100-0020", "2015 Sapphire Dr, San Jose, CA 95101", "200"},
            {"TNT-021", "Ironwood Orthopedics", "ironwoodortho.com", "professional", "admin@ironwoodortho.com", "(555) 100-0021", "2116 Ironwood Ct, Scottsdale, AZ 85251", "30"},
            {"TNT-022", "Prairie Health Cooperative", "prairiehealth.com", "professional", "admin@prairiehealth.com", "(555) 100-0022", "2217 Prairie Ave, Omaha, NE 68101", "65"},
            {"TNT-023", "Ridgeline Pharmacy Network", "ridgelinepharmacy.com", "enterprise", "admin@ridgelinepharmacy.com", "(555) 100-0023", "2318 Ridgeline Blvd, Salt Lake City, UT 84101", "180"},
            {"TNT-024", "Crestview Women's Health", "crestviewwomens.com", "starter", "admin@crestviewwomens.com", "(555) 100-0024", "2419 Crestview Ln, Raleigh, NC 27601", "25"},
            {"TNT-025", "Granite Peak Urgent Care", "granitepeakuc.com", "starter", "admin@granitepeakuc.com", "(555) 100-0025", "2520 Granite Peak Rd, Boise, ID 83701", "20"},
            {"TNT-026", "Bayshore Cardiology", "bayshorecardio.com", "professional", "admin@bayshorecardio.com", "(555) 100-0026", "2621 Bayshore Dr, Tampa, FL 33601", "40"},
            {"TNT-027", "Lakeview Mental Health", "lakeviewmh.com", "professional", "admin@lakeviewmh.com", "(555) 100-0027", "2722 Lakeview Ave, Madison, WI 53701", "35"},
            {"TNT-028", "Aspen Valley Medical Center", "aspenvalleymc.com", "enterprise", "admin@aspenvalleymc.com", "(555) 100-0028", "2823 Aspen Valley Rd, Aspen, CO 81611", "110"},
            {"TNT-029", "Clearwater Health Alliance", "clearwaterha.com", "enterprise", "admin@clearwaterha.com", "(555) 100-0029", "2924 Clearwater Blvd, Orlando, FL 32801", "220"},
            {"TNT-030", "Elderwood Senior Care", "elderwoodsenior.com", "professional", "admin@elderwoodsenior.com", "(555) 100-0030", "3025 Elderwood Dr, Buffalo, NY 14201", "75"},
            {"TNT-031", "Falcon Ridge Imaging", "falconridgeimaging.com", "starter", "admin@falconridgeimaging.com", "(555) 100-0031", "3126 Falcon Ridge Ct, Tucson, AZ 85701", "18"},
            {"TNT-032", "Greenfield Family Practice", "greenfieldfp.com", "starter", "admin@greenfieldfp.com", "(555) 100-0032", "3227 Greenfield Rd, Indianapolis, IN 46201", "15"},
            {"TNT-033", "Harbor Point ENT Specialists", "harborpointent.com", "professional", "admin@harborpointent.com", "(555) 100-0033", "3328 Harbor Point Dr, Baltimore, MD 21201", "28"},
            {"TNT-034", "Keystone Dermatology", "keystonederm.com", "starter", "admin@keystonederm.com", "(555) 100-0034", "3429 Keystone Ave, Pittsburgh, PA 15201", "22"},
            {"TNT-035", "Maple Grove Pediatric Dental", "maplegrovepd.com", "starter", "admin@maplegrovepd.com", "(555) 100-0035", "3530 Maple Grove Blvd, St. Paul, MN 55101", "12"},
            {"TNT-036", "Northwind Health Insurance", "northwindhi.com", "enterprise", "admin@northwindhi.com", "(555) 100-0036", "3631 Northwind Pkwy, Des Moines, IA 50301", "400"},
            {"TNT-037", "Orchard Valley Wellness", "orchardvalley.com", "professional", "admin@orchardvalley.com", "(555) 100-0037", "3732 Orchard Valley Ln, Portland, ME 04101", "45"},
            {"TNT-038", "Palomar Neuroscience Institute", "palomarneuro.com", "professional", "admin@palomarneuro.com", "(555) 100-0038", "3833 Palomar St, San Diego, CA 92101", "55"},
            {"TNT-039", "Quail Creek Oncology", "quailcreekoncology.com", "professional", "admin@quailcreekoncology.com", "(555) 100-0039", "3934 Quail Creek Rd, Oklahoma City, OK 73101", "38"},
            {"TNT-040", "Riverstone Physical Therapy", "riverstonept.com", "starter", "admin@riverstonept.com", "(555) 100-0040", "4035 Riverstone Dr, Louisville, KY 40201", "20"},
            {"TNT-041", "Skyline Respiratory Care", "skylinerespiratory.com", "professional", "admin@skylinerespiratory.com", "(555) 100-0041", "4136 Skyline Blvd, Kansas City, MO 64101", "32"},
            {"TNT-042", "Thornberry Health Plan", "thornberryhp.com", "enterprise", "admin@thornberryhp.com", "(555) 100-0042", "4237 Thornberry Ave, Columbus, OH 43201", "350"},
            {"TNT-043", "Unity Care Physicians", "unitycarephys.com", "professional", "admin@unitycarephys.com", "(555) 100-0043", "4338 Unity Ct, Milwaukee, WI 53201", "60"},
            {"TNT-044", "Vanguard Clinical Research", "vanguardcr.com", "professional", "admin@vanguardcr.com", "(555) 100-0044", "4439 Vanguard Way, Durham, NC 27701", "48"},
            {"TNT-045", "Westfield Gastroenterology", "westfieldgi.com", "starter", "admin@westfieldgi.com", "(555) 100-0045", "4540 Westfield Rd, Westfield, NJ 07090", "16"},
            {"TNT-046", "Xenith Digital Health", "xenithdigital.com", "enterprise", "admin@xenithdigital.com", "(555) 100-0046", "4641 Xenith Dr, Austin, TX 73301", "160"},
            {"TNT-047", "Yellowstone Rural Health", "yellowstonerh.com", "starter", "admin@yellowstonerh.com", "(555) 100-0047", "4742 Yellowstone Hwy, Billings, MT 59101", "10"},
            {"TNT-048", "Zenith Pathology Services", "zenithpath.com", "professional", "admin@zenithpath.com", "(555) 100-0048", "4843 Zenith Ln, Albany, NY 12201", "42"},
            {"TNT-049", "Birchwood Allergy Clinic", "birchwoodallergy.com", "starter", "admin@birchwoodallergy.com", "(555) 100-0049", "4944 Birchwood St, Ann Arbor, MI 48101", "14"},
            {"TNT-050", "Compass Rose Health Network", "compassrosehn.com", "enterprise", "admin@compassrosehn.com", "(555) 100-0050", "5045 Compass Rose Blvd, Dallas, TX 75201", "280"},
            {"TNT-051", "Daybreak Sleep Medicine", "daybreaksleep.com", "starter", "admin@daybreaksleep.com", "(555) 100-0051", "5146 Daybreak Ave, Albuquerque, NM 87101", "12"},
            {"TNT-052", "Emerald City Podiatry", "emeraldcitypod.com", "starter", "admin@emeraldcitypod.com", "(555) 100-0052", "5247 Emerald City Dr, Seattle, WA 98101", "10"},
            {"TNT-053", "Foxglove Naturopathic Medicine", "foxglovenaturo.com", "starter", "admin@foxglovenaturo.com", "(555) 100-0053", "5348 Foxglove Ln, Asheville, NC 28801", "8"},
            {"TNT-054", "Golden Gate Dental Group", "goldengatedral.com", "professional", "admin@goldengatedental.com", "(555) 100-0054", "5449 Golden Gate Ave, San Francisco, CA 94102", "55"},
            {"TNT-055", "Highland Spine Center", "highlandspine.com", "professional", "admin@highlandspine.com", "(555) 100-0055", "5550 Highland Rd, Knoxville, TN 37901", "30"},
            {"TNT-056", "Ivy League Fertility", "ivyleaguefertility.com", "professional", "admin@ivyleaguefertility.com", "(555) 100-0056", "5651 Ivy Ln, New Haven, CT 06501", "35"},
            {"TNT-057", "Juniper Health Cooperative", "juniperhealthcoop.com", "professional", "admin@juniperhealthcoop.com", "(555) 100-0057", "5752 Juniper Way, Santa Fe, NM 87501", "50"},
            {"TNT-058", "Kingsport Regional Hospital", "kingsportregional.com", "enterprise", "admin@kingsportregional.com", "(555) 100-0058", "5853 Kingsport Blvd, Kingsport, TN 37660", "140"},
            {"TNT-059", "Linden Grove Home Health", "lindengrovehealth.com", "professional", "admin@lindengrovehealth.com", "(555) 100-0059", "5954 Linden Grove Dr, Grand Rapids, MI 49501", "45"},
            {"TNT-060", "Monarch Behavioral Sciences", "monarchbehavioral.com", "professional", "admin@monarchbehavioral.com", "(555) 100-0060", "6055 Monarch Ave, Richmond, VA 23218", "38"},
            {"TNT-061", "Nova Pain Management", "novapainmgmt.com", "starter", "admin@novapainmgmt.com", "(555) 100-0061", "6156 Nova Ct, Las Vegas, NV 89101", "18"},
            {"TNT-062", "Oakridge Laboratory Services", "oakridgelabs.com", "professional", "admin@oakridgelabs.com", "(555) 100-0062", "6257 Oakridge Rd, Oak Ridge, TN 37830", "52"},
            {"TNT-063", "Pioneer Rehab Hospital", "pioneerrehab.com", "enterprise", "admin@pioneerrehab.com", "(555) 100-0063", "6358 Pioneer Blvd, Salt Lake City, UT 84101", "100"},
            {"TNT-064", "Quorum Health Analytics", "quorumhealth.com", "enterprise", "admin@quorumhealth.com", "(555) 100-0064", "6459 Quorum Dr, Reston, VA 20190", "190"},
            {"TNT-065", "Rainbow Pediatric Hospital", "rainbowpeds.com", "enterprise", "admin@rainbowpeds.com", "(555) 100-0065", "6560 Rainbow Pkwy, Kansas City, MO 64101", "160"},
            {"TNT-066", "Stonegate Ophthalmology", "stonegateeye.com", "professional", "admin@stonegateeye.com", "(555) 100-0066", "6661 Stonegate Ave, Fort Worth, TX 76101", "28"},
            {"TNT-067", "Timberland Rural Clinics", "timberlandclinics.com", "professional", "admin@timberlandclinics.com", "(555) 100-0067", "6762 Timberland Rd, Eugene, OR 97401", "40"},
            {"TNT-068", "Uptown Integrative Medicine", "uptownintegrative.com", "starter", "admin@uptownintegrative.com", "(555) 100-0068", "6863 Uptown Blvd, Minneapolis, MN 55401", "15"},
            {"TNT-069", "Valley Forge Health Plan", "valleyforgehp.com", "enterprise", "admin@valleyforgehp.com", "(555) 100-0069", "6964 Valley Forge Dr, King of Prussia, PA 19406", "450"},
            {"TNT-070", "Windermere Geriatric Care", "windermeregeriatric.com", "professional", "admin@windermeregeriatric.com", "(555) 100-0070", "7065 Windermere Ln, Winter Park, FL 32789", "55"},
            {"TNT-071", "Apex Radiology Partners", "apexradiology.com", "professional", "admin@apexradiology.com", "(555) 100-0071", "7166 Apex Dr, Raleigh, NC 27601", "48"},
            {"TNT-072", "BluePeak Health Insurance", "bluepeakhi.com", "enterprise", "admin@bluepeakhi.com", "(555) 100-0072", "7267 BluePeak Ave, Denver, CO 80201", "380"},
            {"TNT-073", "Cypress Creek Endocrinology", "cypresscreekendo.com", "starter", "admin@cypresscreekendo.com", "(555) 100-0073", "7368 Cypress Creek Rd, Houston, TX 77001", "16"},
            {"TNT-074", "Driftwood Hospice Services", "driftwoodhospice.com", "professional", "admin@driftwoodhospice.com", "(555) 100-0074", "7469 Driftwood Ln, Savannah, GA 31401", "42"},
            {"TNT-075", "Eastgate Rheumatology", "eastgaterheum.com", "starter", "admin@eastgaterheum.com", "(555) 100-0075", "7570 Eastgate Blvd, Cincinnati, OH 45201", "14"},
            {"TNT-076", "Frostline Cryotherapy Center", "frostlinecryo.com", "starter", "admin@frostlinecryo.com", "(555) 100-0076", "7671 Frostline Dr, Anchorage, AK 99501", "8"},
            {"TNT-077", "Garnet Valley Obstetrics", "garnetvalleyob.com", "professional", "admin@garnetvalleyob.com", "(555) 100-0077", "7772 Garnet Valley Rd, Media, PA 19063", "35"},
            {"TNT-078", "Hearthstone Ambulatory Surgery", "hearthstoneasc.com", "professional", "admin@hearthstoneasc.com", "(555) 100-0078", "7873 Hearthstone Way, San Antonio, TX 78201", "50"},
            {"TNT-079", "Indigo Health Technologies", "indigoht.com", "enterprise", "admin@indigoht.com", "(555) 100-0079", "7974 Indigo Blvd, Boston, MA 02101", "210"},
            {"TNT-080", "Jade Mountain Acupuncture", "jademtnacup.com", "starter", "admin@jademtnacup.com", "(555) 100-0080", "8075 Jade Mountain Rd, Sedona, AZ 86336", "6"},
            {"TNT-081", "Kendall Square Biotech Health", "kendallsqbio.com", "enterprise", "admin@kendallsqbio.com", "(555) 100-0081", "8176 Kendall Sq, Cambridge, MA 02139", "170"},
            {"TNT-082", "Laurelwood Psychiatric Services", "laurelwoodpsych.com", "professional", "admin@laurelwoodpsych.com", "(555) 100-0082", "8277 Laurelwood Dr, Portland, OR 97201", "45"},
            {"TNT-083", "Mesa Verde Occupational Health", "mesaverdeoh.com", "professional", "admin@mesaverdeoh.com", "(555) 100-0083", "8378 Mesa Verde Ave, Durango, CO 81301", "30"},
            {"TNT-084", "Northcrest Wound Care", "northcrestwound.com", "starter", "admin@northcrestwound.com", "(555) 100-0084", "8479 Northcrest Dr, Topeka, KS 66601", "12"},
            {"TNT-085", "Oceanview Pulmonology", "oceanviewpulm.com", "professional", "admin@oceanviewpulm.com", "(555) 100-0085", "8580 Oceanview Blvd, Virginia Beach, VA 23451", "25"},
            {"TNT-086", "Pebblestone Health Group", "pebblestonehealth.com", "enterprise", "admin@pebblestonehealth.com", "(555) 100-0086", "8681 Pebblestone Ct, Memphis, TN 38101", "135"},
            {"TNT-087", "Quartzite Hearing Center", "quartzitehearing.com", "starter", "admin@quartzitehearing.com", "(555) 100-0087", "8782 Quartzite Rd, Flagstaff, AZ 86001", "10"},
            {"TNT-088", "Rosemary Hill Midwifery", "rosemaryhillmw.com", "starter", "admin@rosemaryhillmw.com", "(555) 100-0088", "8883 Rosemary Hill Dr, Charlottesville, VA 22901", "8"},
            {"TNT-089", "Starlite Veterinary Hospital", "starlitevethospital.com", "professional", "admin@starlitevet.com", "(555) 100-0089", "8984 Starlite Ave, Lexington, KY 40501", "40"},
            {"TNT-090", "Tidewater Health Exchange", "tidewaterhe.com", "enterprise", "admin@tidewaterhe.com", "(555) 100-0090", "9085 Tidewater Pkwy, Norfolk, VA 23501", "240"},
            {"TNT-091", "Uplift Sports Medicine", "upliftsportsmed.com", "professional", "admin@upliftsportsmed.com", "(555) 100-0091", "9186 Uplift Dr, Colorado Springs, CO 80901", "35"},
            {"TNT-092", "Verdant Life Sciences", "verdantlife.com", "enterprise", "admin@verdantlife.com", "(555) 100-0092", "9287 Verdant Way, Research Triangle, NC 27709", "195"},
            {"TNT-093", "Whitfield Community Health", "whitfieldch.com", "professional", "admin@whitfieldch.com", "(555) 100-0093", "9388 Whitfield Rd, Dalton, GA 30720", "55"},
            {"TNT-094", "Xerion Precision Diagnostics", "xeriondiag.com", "professional", "admin@xeriondiag.com", "(555) 100-0094", "9489 Xerion Blvd, San Diego, CA 92101", "48"},
            {"TNT-095", "Yarrow Creek Wellness Spa", "yarrowcreekwellness.com", "starter", "admin@yarrowcreekwellness.com", "(555) 100-0095", "9590 Yarrow Creek Ln, Park City, UT 84060", "10"},
            {"TNT-096", "Zephyr Mobile Health", "zephyrmobilehealth.com", "professional", "admin@zephyrmobilehealth.com", "(555) 100-0096", "9691 Zephyr Dr, Tempe, AZ 85281", "30"},
            {"TNT-097", "Arbor Health Partners", "arborhealthpartners.com", "enterprise", "admin@arborhealthpartners.com", "(555) 100-0097", "9792 Arbor Ave, Ann Arbor, MI 48104", "155"},
            {"TNT-098", "BrightPath Telehealth", "brightpathtele.com", "professional", "admin@brightpathtele.com", "(555) 100-0098", "9893 BrightPath Blvd, Reston, VA 20191", "65"},
            {"TNT-099", "Canyon Rock Health System", "canyonrockhs.com", "enterprise", "admin@canyonrockhs.com", "(555) 100-0099", "9994 Canyon Rock Dr, Sedona, AZ 86336", "185"},
            {"TNT-100", "Diamond Peak Insurance Group", "diamondpeakins.com", "enterprise", "admin@diamondpeakins.com", "(555) 100-0100", "10095 Diamond Peak Rd, Reno, NV 89501", "320"},
        };

        List<Tenant> tenants = new ArrayList<>();
        for (String[] data : tenantData) {
            Tenant tenant = Tenant.builder()
                    .tenantId(data[0])
                    .name(data[1])
                    .displayName(data[1])
                    .domain(data[2])
                    .status(TenantStatus.ACTIVE)
                    .plan(data[3])
                    .contactEmail(data[4])
                    .contactPhone(data[5])
                    .address(data[6])
                    .maxUsers(Integer.parseInt(data[7]))
                    .build();
            tenants.add(tenant);
        }

        return tenants;
    }
}
