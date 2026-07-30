import { Pencil, Trash2, Calendar, CheckCircle, Clock, Check } from 'lucide-react';
import { useState, useEffect, useCallback } from 'react';
import { Badge } from 'src/shared/ui/badge';
import { Button } from 'src/shared/ui/button';
import { Card, CardHeader, CardTitle, CardContent } from 'src/shared/ui/card';
import { AppointmentModal } from './modals/appointment-modal';
import type { Appointment } from '../../../shared/model';
import { DataTable } from 'src/shared/ui/data-table';
import { toaster } from 'src/shared/ui/toaster';

const statusVariant: Record<string, 'warning' | 'success' | 'neutral' | 'destructive'> = {
    PENDING: 'warning',
    'En attente': 'warning',
    CONFIRMED: 'success',
    Confirmé: 'success',
    COMPLETED: 'neutral',
    Terminé: 'neutral',
    Annulé: 'destructive',
};

export const AppointmentsPage = () => {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [appointments, setAppointments] = useState<Appointment[]>([]);
    const [filteredAppointments, setFilteredAppointments] = useState<Appointment[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [selectedAppointment, setSelectedAppointment] = useState<Appointment | null>(null);
    const [tempStatus, setTempStatus] = useState<string>('');
    const [isUpdating, setIsUpdating] = useState(false);
    const [newAppointment, setNewAppointment] = useState({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        date: '',
        time: '',
        service: 'Consultation générale'
    });
    const [currentPage] = useState(0);
    const apiBase = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1';

    const fetchAppointments = useCallback(async () => {
        setIsLoading(true);
        try {
            const response = await fetch(`${apiBase}/appointments?page=${currentPage}&size=5`);
            if (!response.ok) throw new Error('Failed to fetch');
            const data = await response.json();
            const content = data.content || data;
            const mapped: Appointment[] = content.map((app: {
                id: string;
                appointmentDate: string;
                status: Appointment['status'];
                user?: { firstName: string; lastName: string; email: string; phone: string };
                service?: { name: string };
            }) => {
                const dateStr = app.appointmentDate || '';
                const dateObj = dateStr ? new Date(dateStr) : new Date();
                return {
                    id: app.id || '',
                    patient: app.user ? `${app.user.firstName} ${app.user.lastName}` : 'Inconnu',
                    email: app.user ? app.user.email : '',
                    phone: app.user ? app.user.phone : '',
                    service: app.service ? app.service.name : 'Service inconnu',
                    date: dateStr ? dateStr.split('T')[0] : '',
                    time: dateStr ? dateObj.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' }) : '',
                    status: app.status || 'En attente'
                };
            });

            setAppointments(mapped);
            setFilteredAppointments(mapped);
        } catch (error) {
            console.error('Error fetching appointments:', error);
        } finally {
            setIsLoading(false);
        }
    }, [apiBase, currentPage]);

    useEffect(() => {
        fetchAppointments();
    }, [fetchAppointments]);

    const handleUpdateStatus = async (id: string, newStatus: string) => {
        setIsUpdating(true);
        try {
            const response = await fetch(`${apiBase}/appointments/${id}/status`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus }),
            });

            if (!response.ok) throw new Error('Failed to update status');
            fetchAppointments();
            setIsModalOpen(false);
            setSelectedAppointment(null);
            toaster.create({ title: 'Succès', description: 'Statut mis à jour avec succès !', type: 'success' });
        } catch (error) {
            console.error('Error updating status:', error);
        } finally {
            setIsUpdating(false);
        }
    };

    const openEditModal = (app: Appointment) => {
        setSelectedAppointment(app);
        setTempStatus(app.status);
        setIsModalOpen(true);
    };

    const handleCreateAppointment = async () => {
        setIsUpdating(true);
        try {
            const response = await fetch(`${apiBase}/appointments/public`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(newAppointment),
            });

            if (!response.ok) throw new Error('Failed to create appointment');

            fetchAppointments();
            setIsModalOpen(false);
            setNewAppointment({
                firstName: '',
                lastName: '',
                email: '',
                phone: '',
                date: '',
                time: '',
                service: 'Consultation générale'
            });
            toaster.create({ title: 'Succès', description: 'Rendez-vous créé avec succès !', type: 'success' });
        } catch (error) {
            console.error('Error creating appointment:', error);
        } finally {
            setIsUpdating(false);
        }
    };
    const formatDate = (dateStr: string) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        return date.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short', year: 'numeric' });
    };

    const confirmedCount = appointments.filter(app => app.status === 'Confirmé' || app.status === 'CONFIRMED').length;
    const pendingCount = appointments.filter(app => app.status === 'En attente' || app.status === 'PENDING').length;
    const completedCount = appointments.filter(app => app.status === 'Terminé' || app.status === 'COMPLETED').length;

    return (
        <div>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
                {[
                    { label: "Total RDV", icon: Calendar, iconColor: "text-muted-foreground", value: appointments.length, valueColor: "" },
                    { label: "Confirmés", icon: CheckCircle, iconColor: "text-green-500", value: confirmedCount, valueColor: "text-green-500" },
                    { label: "En attente", icon: Clock, iconColor: "text-yellow-500", value: pendingCount, valueColor: "text-yellow-500" },
                    { label: "Terminés", icon: Check, iconColor: "text-[var(--accent)]", value: completedCount, valueColor: "text-[var(--accent)]" }
                ].map((stat, i) => (
                    <Card key={i}>
                        <CardHeader className="flex flex-row items-center justify-between pb-2">
                            <CardTitle className="text-sm font-medium">{stat.label}</CardTitle>
                            <stat.icon className={`h-4 w-4 ${stat.iconColor}`} />
                        </CardHeader>
                        <CardContent>
                            <div className={`text-2xl font-bold ${stat.valueColor}`}>{stat.value}</div>
                        </CardContent>
                    </Card>
                ))}
            </div>

            <DataTable<Appointment>
                columns={[
                    { id: 'ref', header: 'Réf', cell: (row) => <span className="font-medium text-muted-foreground">{row.id.substring(0, 8)}</span> },
                    { id: 'patient', header: 'Patient', cell: (row) => <span className="font-semibold">{row.patient}</span> },
                    { id: 'contact', header: 'Contact', cell: (row) => <div><p className="text-sm">{row.email}</p><p className="text-xs text-muted-foreground">{row.phone}</p></div> },
                    { id: 'service', header: 'Service', cell: (row) => row.service },
                    { id: 'date', header: 'Date & Heure', cell: (row) => <div><p className="font-medium">{formatDate(row.date)}</p><p className="text-xs text-muted-foreground">à {row.time}</p></div> },
                    { id: 'status', header: 'Statut', cell: (row) => <Badge variant={statusVariant[row.status]}>{row.status}</Badge> },
                    {
                        id: 'actions', header: 'Actions', cell: (row) => (
                            <div className="flex gap-2">
                                <Button variant="ghost" size="icon" onClick={() => openEditModal(row)} className="p-2 bg-[rgba(5,199,226,0.1)] text-[var(--accent)] rounded-md hover:bg-[rgba(5,199,226,0.2)] transition-colors">
                                    <Pencil className="w-4 h-4" />
                                </Button>
                                <Button variant="ghost" size="icon" className="p-2 bg-red-600/10 text-red-600 rounded-md hover:bg-red-600/20 transition-colors">
                                    <Trash2 className="w-4 h-4" />
                                </Button>
                            </div>
                        )
                    },
                ]}
                data={filteredAppointments}
                getRowId={(row) => row.id}
                isLoading={isLoading}
                emptyMessage="Aucun rendez-vous trouvé"
            />

            <AppointmentModal
                isOpen={isModalOpen}
                onClose={() => { setIsModalOpen(false); setSelectedAppointment(null); }}
                selectedAppointment={selectedAppointment}
                tempStatus={tempStatus}
                setTempStatus={setTempStatus}
                isUpdating={isUpdating}
                handleUpdateStatus={handleUpdateStatus}
                newAppointment={newAppointment}
                setNewAppointment={setNewAppointment}
                handleCreateAppointment={handleCreateAppointment}
            />

        </div>
    );
};